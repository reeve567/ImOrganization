package dev.reeve.organization.game

import dev.reeve.organization.Main
import dev.reeve.organization.game.info.GameFiles
import dev.reeve.organization.game.info.getGameFiles
import dev.reeve.organization.getVersion
import dev.reeve.organization.gui.LogWindow
import dev.reeve.organization.gui.MainWindow
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level
import kotlin.collections.ArrayList

class GameManager(private val main: Main, private val iconManager: IconManager) {
	private val dataGrabber = DataGrabber(main)
	private val config = main.config
	private val icons = main.icons
	private var searching = false
	private var unpacking = false
	private var saving = false
	
	var games: List<GameInfo> = dataGrabber.getGameNotes(emptyList())
	
	fun unpack() {
		LogWindow.addLog("Unpacking...", Level.INFO)
		unpacking = true
		synchronized(games) {
			games = emptyList()
			
			var done = false
			val delete = ArrayList<File>()
			while (!done) {
				done = true
				delete.forEach {
					LogWindow.addLog("Deleted ${it.name}: ${it.deleteRecursively()}", Level.INFO)
				}
				delete.clear()
				for (file in main.unorganized.listFiles()) {
					var start = Date().time
					if (file.isDirectory) {
						continue
					}
					if (file.extension != "bat" && file.extension != "exe" && file.extension != "mega") {
						var gameFiles: GameFiles? = null
						
						runBlocking {
							gameFiles = getGameFiles(file, config, main.temp, getDate = true, getIcon = icons)
						}
						
						var name = gameFiles!!.name
						if (config.conversions.containsKey(name)) {
							name = config.conversions[name] ?: error("how")
						}
						
						if (name != "???" && name != "Game") {
							var difference = (Date().time - start) / 1000.0
							LogWindow.addLog("${file.name} moving to $name (${difference}s)")
							val dir = File(main.games, name)
							if (!dir.exists())
								dir.mkdirs()
							
							// copy the zip into gameDir / delete old zip
							
							val newFile = File(dir, file.name)
							start = Date().time
							
							fun moveGame() {
								runBlocking {
									gameFiles!!.iconInputStreamData?.apply {
										iconManager.setIcon(gameFiles!!)
									} ?: {
										if (icons) {
											gameFiles?.icon?.apply {
												iconManager.setIcon(gameFiles!!)
											}
										}
									}
								}
								
								gameFiles?.closeable?.close()
								
								Files.move(
									file.toPath(),
									newFile.toPath(),
									StandardCopyOption.ATOMIC_MOVE,
									StandardCopyOption.REPLACE_EXISTING
								)
								newFile.setLastModified(gameFiles!!.lastUpdated!!.time)
								dir.setLastModified(System.currentTimeMillis())
								
								difference = (Date().time - start) / 1000.0
								LogWindow.addLog("${file.name} moved to $name (${difference}s)")
								done = false
							}
							
							if (!newFile.exists()) {
								moveGame()
								break
							} else {
								if (newFile.totalSpace == file.totalSpace) {
									LogWindow.addLog("Already exists")
									gameFiles?.closeable?.close()
									done = false
									delete.add(file)
									break
								} else {
									newFile.deleteRecursively()
									moveGame()
									break
								}
							}
						}
					}
					
				}
			}
		}
		unpacking = false
		search()
		saveAll()
	}
	
	fun search(filter: List<String> = emptyList()) {
		LogWindow.addLog("Refreshing...")
		searching = true
		synchronized(games) {
			games = emptyList()
			games = dataGrabber.getGameNotes(filter)
		}
		searching = false
	}
	
	fun saveAll() {
		LogWindow.addLog("Saving...")
		saving = true
		synchronized(games) {
			games.forEach {
				synchronized(it) {
					it.save()
				}
			}
		}
		saving = false
	}
	
	fun checkVersions() {
		val unplayedGames = mutableListOf<String>()
		val versions = mutableListOf<String>()
		
		for (file in main.games.listFiles()) {
			if (file.isDirectory) {
				val files = file.listFiles { f ->
					f.isDirectory || (f.isFile)
				}
				
				val directory = file.listFiles().first {
					it.isDirectory
				} != null
				
				if (!files.isNullOrEmpty()) {
					var newest = files[0]
					
					files.forEach {
						if (it.lastModified() > newest.lastModified()) {
							newest = it
						}
					}
					val fileName = if (newest.isDirectory) newest.name else newest.nameWithoutExtension
					val gameFiles: GameFiles
					
					runBlocking {
						gameFiles = getGameFiles(file, config, main.temp, getExe = false)
					}
					
					val pattern = "MM-dd--yyyy"
					val simpleDateFormat = SimpleDateFormat(pattern)
					val date = simpleDateFormat.format(gameFiles.lastUpdated)
					
					val version = getVersion(fileName)
					if (version == "???") {
						println("Could not get version for ${file.name} ($fileName)")
					} else {
						if (directory) {
							unplayedGames.add("[$date] ${file.name} -- $version")
						}
						
						versions.add("[$date] ${file.name} -- $version")
					}
				}
			}
		}
	}
	
	suspend fun waitForAvailable() {
		val tries = 3
		for (i in 0 .. tries) {
			while (isNotAvailable()) {
				delay(100)
			}
			delay(100)
		}
	}
	
	fun isAvailable(): Boolean {
		return !(saving || searching || unpacking)
	}
	
	fun isNotAvailable(): Boolean {
		return !isAvailable()
	}
}