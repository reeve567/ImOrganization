package dev.reeve.organization

import dev.reeve.organization.game.GameManager
import dev.reeve.organization.game.IconManager
import dev.reeve.organization.gui.LogWindow
import dev.reeve.organization.gui.MainWindow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import java.util.logging.Level

class Main(location: String?) {
	private var baseLocation = File("D:\\")
	private var configFile: File? = null
	private var _config: Config? = null
	val config: Config
		get() : Config {
			if (_config == null) {
				configFile = File(baseLocation, "config.json")
				if (!configFile!!.exists()) {
					LogWindow.addLog("No config found", Level.WARNING)
					configFile!!.createNewFile()
					_config = Config()
					_config!!.dataLocation = configFile!!
					configFile!!.writeText(Config.gson.toJson(_config))
				} else {
					LogWindow.addLog("Loaded config")
					_config = Config.gson.fromJson(FileReader(configFile!!), Config::class.java)
					_config!!.dataLocation = configFile!!
				}
			}
			return _config!!
		}
	
	val unorganized: File
	val games: File
	val temp: File
	val unren: File
	val icons = true
	lateinit var mainWindow: MainWindow
	
	val iconManager: IconManager
	val gameManager: GameManager
	
	init {
		if (location != null && location != "") {
			baseLocation = File(location)
		}
		
		if (!baseLocation.exists()) {
			baseLocation = File("./")
		}
		
		unorganized = File(baseLocation, "unorganized")
		games = File(baseLocation, "games")
		temp = File(baseLocation, ".temp")
		unren = File(baseLocation, "UnRen-dev.bat")
		mkdirs()
		println(baseLocation.absolutePath)
		
		iconManager = IconManager(this)
		gameManager = GameManager(this, iconManager)
		var done = false
		
		GlobalScope.launch {
			mainWindow = MainWindow(this@Main)
			done = true
		}
		
		runBlocking {
			while (!done) {
				delay(200)
			}
		}
		
		try {
			while (mainWindow.isOpen()) {
				runBlocking {
					delay(100)
					// still running...
				}
			}
		} catch (ignored: NullPointerException) {
			println("NPE Exit")
		}
	}
	
	private fun mkdirs() {
		if (!unorganized.exists()) {
			unorganized.mkdirs()
		}
		
		if (!games.exists()) {
			games.mkdirs()
		}
		
		if (!temp.exists()) {
			temp.mkdirs()
			Files.setAttribute(temp.toPath(), "dos:hidden", true)
		}
	}
}