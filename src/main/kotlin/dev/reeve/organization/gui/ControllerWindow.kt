package dev.reeve.organization.gui

import dev.reeve.organization.Config
import dev.reeve.organization.Main
import dev.reeve.organization.game.GameManager
import dev.reeve.organization.ifRun
import imgui.ImGui
import imgui.WindowFlag
import imgui.WindowFlags
import imgui.demo.ShowDemoWindowWidgets
import java.awt.Desktop
import java.util.logging.Level

class ControllerWindow(
	private val gameManager: GameManager,
	private val main: Main,
	private val mainWindow: MainWindow,
	window: Config.WindowInformation.Window,
) : ImGuiWindow("Controller", null, window) {
	companion object {
		var show = true
		var showGames = false
		var showGameInfo = false
		var showGameFiles = false
		var showLog = false
		var showDemo = false
	}
	
	override fun setFlags(): WindowFlags {
		return WindowFlag.AlwaysAutoResize.or(WindowFlag.NoResize)
	}
	
	override fun invoke(imGui: ImGui) {
		with(imGui) {
			button("Unzip").ifRun {
				gameManager.unpack()
			}
			sameLine()
			button("Refresh").ifRun {
				gameManager.search()
			}
			sameLine()
			button("Open Config").ifRun {
				Desktop.getDesktop()?.open(main.config.dataLocation)
			}
			button("Save Windows").ifRun {
				LogWindow.addLog("Saving windows...")
				mainWindow.saveWindows()
			}
			
			checkbox("Show games list", ::showGames)
			checkbox("Show game info", ::showGameInfo)
			checkbox("Show log", ::showLog)
			checkbox("Show demo", ::showDemo)
			checkbox("Save windows", main.config::saveWindows)
			
			if (showDemo) {
				showDemoWindow(::showDemo)
				ShowDemoWindowWidgets
			}
		}
	}
}