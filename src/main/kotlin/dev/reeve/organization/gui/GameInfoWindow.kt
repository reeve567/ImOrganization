package dev.reeve.organization.gui

import dev.reeve.organization.Config
import dev.reeve.organization.Main
import dev.reeve.organization.game.GameInfo
import dev.reeve.organization.game.info.ArtStyle
import dev.reeve.organization.game.info.EngineType
import dev.reeve.organization.ifRun
import dev.reeve.organization.loadTexture
import glm_.glm
import glm_.vec2.Vec2
import imgui.*
import imgui.dsl.child
import imgui.dsl.group
import org.lwjgl.opengl.GL
import uno.kotlin.file
import java.awt.Desktop
import java.io.File
import java.net.URI
import javax.imageio.ImageIO
import kotlin.reflect.KMutableProperty0

class GameInfoWindow(
	private val gamesWindow: GamesWindow,
	private val main: Main,
	window: Config.WindowInformation.Window,
) : ImGuiWindow("Game Info", ControllerWindow::showGameInfo, window) {
	private var selectedArtStyle = -1
	private var selectedEngine = -1
	
	// checkboxes
	private var playedLatest = false
	private var developmentCompleted = false
	private var personallyCompleted = false
	
	// textboxes
	private var url = ""
	private var walkthrough = ""
	private var notes = ""
	private var title = ""
	
	private var lastGame = ""
	private var gameExecutable = ""
	private var dirty = false
	
	private val callback: InputTextCallback = { data ->
		if (data.eventFlag != InputTextFlag.CallbackResize.i) {
			val string = (data.userData as KMutableProperty0<String>)
			string.set(String(data.buf))
		}
		false // pass return?
	}
	
	private fun reset(game: GameInfo) {
		dirty = false
		
		selectedArtStyle = game.artStyle?.ordinal ?: -1
		selectedEngine = game.engineType?.ordinal ?: -1
		playedLatest = game.playedLatest
		developmentCompleted = game.developmentCompleted
		personallyCompleted = game.personallyCompleted
		url = game.url ?: ""
		walkthrough = game.walkthrough ?: ""
		notes = game.notes ?: ""
		title = game.lastTitle ?: ""
		gameExecutable = game.gameExecutable ?: ""
	}
	
	private fun save(game: GameInfo) {
		dirty = false
		
		if (selectedArtStyle != -1) {
			game.artStyle = ArtStyle.values()[selectedArtStyle]
		}
		if (selectedEngine != -1) {
			game.engineType = EngineType.values()[selectedEngine]
		}
		
		game.playedLatest = playedLatest
		game.developmentCompleted = developmentCompleted
		game.personallyCompleted = personallyCompleted
		game.url = url
		game.walkthrough = walkthrough
		game.notes = notes
		game.lastTitle = title
		
		game.save()
	}
	
	override fun setFlags(): WindowFlags {
		return WindowFlag.AlwaysAutoResize.i
	}
	
	override fun invoke(imGui: ImGui) {
		with(imGui) {
			if (!gamesWindow.selecting) {
				if (gamesWindow.selectedGame != null) {
					val game = gamesWindow.selectedGame!!
					
					if (lastGame != game.name) {
						reset(game)
						lastGame = game.name!!
					}
					
					/*if (game.getPicture() != null) {
						val image = ImageIO.read(game.getPicture())
						
						val change = windowContentRegionWidth / image.width
						//image(loadTexture(image), Vec2(image.width * change,image.height * change))
					}*/
					
					text(game.name!! + if (dirty) " (Dirty)" else "")
					
					if (button("Save")) {
						save(game)
					}; sameLine()
					if (button("Reset")) {
						reset(game)
					}; sameLine()
					button("Open URL").ifRun {
						if (game.url != null)
							Desktop.getDesktop()?.browse(URI(game.url!!))
					}; sameLine()
					button("Open folder").ifRun {
						Desktop.getDesktop()?.open(game.dataFile!!.parentFile)
					}; sameLine()
					button("Refresh from URL").ifRun {
						val data = game.getFromURL(main.config, true)
						if (data != null) {
							LogWindow.addLog("Applying information...")
							LogWindow.addLog(data.toString())
							selectedEngine = data.first.ordinal
							developmentCompleted = data.second
							game.tags = data.third
							save(game)
						}
					}; sameLine()
					button("Print").ifRun {
						LogWindow.addLog("${game.name!!} as JSON:\n" + Config.gson.toJson(game).replace("\\u0000",""))
					}
					
					child("left", Vec2(200, 300), true) {
						text("Tags:")
						separator()
						if (game.tags != null && game.tags!!.size > 0) {
							for (element in game.tags!!) {
								text(element)
							}
						}
					}
					
					sameLine()
					
					child("middle", Vec2(250, 300), true) {
						checkbox("Played latest", ::playedLatest).markDirty()
						checkbox("Personally Completed", ::personallyCompleted).markDirty()
						checkbox("Development Completed", ::developmentCompleted).markDirty()
						separator()
						group {
							group {
								text("Art Style:")
								ArtStyle.values().forEach {
									radioButton(it.displayLabel, ::selectedArtStyle, it.ordinal).markDirty()
								}
							}
							sameLine()
							group {
								text("Engine:")
								EngineType.values().forEach {
									radioButton(it.displayLabel, ::selectedEngine, it.ordinal).markDirty()
								}
							}
						}
					}
					
					sameLine()
					
					child("right", Vec2(400, 300), true) {
						inputText("URL",
							url,
							InputTextFlag.CallbackAlways.or(InputTextFlag.EnterReturnsTrue),
							callback,
							::url).markDirty()
						
						inputText("Walkthrough",
							walkthrough,
							InputTextFlag.CallbackAlways.or(InputTextFlag.EnterReturnsTrue),
							callback,
							::walkthrough).markDirty()
						
						inputText("Title",
							title.toByteArray(),
							InputTextFlag.CallbackAlways.or(InputTextFlag.EnterReturnsTrue),
							callback,
							::title).markDirty()
						
						separator()
						text("Notes/Codes")
						inputTextMultiline("#Notes",
							notes.toByteArray(notes.length + 5),
							Vec2(400, 150),
							InputTextFlag.CallbackAlways.or(InputTextFlag.CallbackResize),
							callback,
							::notes).markDirty()
						
					}
					// used to be 850
					child("files", Vec2(windowContentRegionWidth, 300), true) {
						text("Files:")
						separator()
						for (file in game.dataFile!!.parentFile.listFiles()) {
							text(file.name)
						}
					}
					
				} else {
					text("No game selected")
				}
			}
		}
	}
	
	fun Boolean.markDirty() {
		this.ifRun { dirty = true }
	}
}