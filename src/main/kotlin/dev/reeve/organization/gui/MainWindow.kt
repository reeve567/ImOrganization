package dev.reeve.organization.gui

import dev.reeve.organization.Config
import dev.reeve.organization.Main
import glm_.vec2.Vec2
import glm_.vec2.Vec2i
import glm_.vec4.Vec4
import imgui.ImGui
import imgui.classes.Context
import imgui.impl.gl.ImplGL3
import imgui.impl.gl.glslVersion
import imgui.impl.glfw.ImplGlfw
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack
import uno.glfw.GlfwWindow
import uno.glfw.VSync
import uno.glfw.glfw

class MainWindow(private val main: Main) {
	private val window: GlfwWindow
	private val context: Context
	private val implGlfw: ImplGlfw
	private val implGL3: ImplGL3
	
	// windows
	private var controllerWindow: ControllerWindow
	private var gamesWindow: GamesWindow
	private var gameInfoWindow: GameInfoWindow
	private var logWindow: LogWindow
	
	private val backgroundColor = Vec4(.72f, .72f, .72f, 1f)
	
	init {
		glfw {
			errorCallback = { error, description -> println("Glfw error $error: $description") }
			init()
			windowHint {
				glslVersion = 130
				context.version = "3.0"
			}
		}
		var width = 1280
		var height = 720
		
		val outerWindow = main.config.windowInfo.outerWindow
		if (!outerWindow.size.array.all { it == 0f }) {
			width = outerWindow.size.x.toInt()
			height = outerWindow.size.y.toInt()
		}
		window = GlfwWindow(width, height, "ImOrganization")
		window.makeContextCurrent()
		glfw.swapInterval = VSync.Adaptive
		
		GL.createCapabilities()
		
		context = Context()
		
		ImGui.styleColorsDark()
		
		implGlfw = ImplGlfw(window, true)
		implGL3 = ImplGL3()
		
		if (!outerWindow.location.toFloatArray().all { it == 0f }) {
			window.pos = Vec2i(outerWindow.location.array.map { it.toInt() })
		}
		
		// initialize windows
		logWindow = LogWindow(main.config.windowInfo.logWindow)
		controllerWindow = ControllerWindow(main.gameManager, main, this, main.config.windowInfo.controllerWindow)
		gamesWindow = GamesWindow(main.gameManager, main.config.windowInfo.gamesListWindow)
		gameInfoWindow = GameInfoWindow(gamesWindow, main, main.config.windowInfo.gamesInfoWindow)
		
		window.loop(::mainLoop)
		
		if (main.config.saveWindows) {
			saveWindows()
		}
		
		main.config.save()
		
		
		
		implGL3.shutdown()
		implGlfw.shutdown()
		context.destroy()
		window.destroy()
		glfw.terminate()
	}
	
	fun mainLoop(stack: MemoryStack) {
		implGL3.newFrame()
		implGlfw.newFrame()
		
		ImGui.run {
			newFrame()
			
			run(controllerWindow::run)
			run(gamesWindow::run)
			run(gameInfoWindow::run)
			run(logWindow::run)
		}
		
		ImGui.render()
		
		val viewport = window.frameSize
		
		GL11.glViewport(viewport.x, viewport.y, viewport.z, viewport.w)
		GL11.glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w)
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
		
		implGL3.renderDrawData(ImGui.drawData!!)
	}
	
	fun isOpen(): Boolean {
		return window != null && window.isOpen
	}
	
	fun saveWindows() {
		val info = main.config.windowInfo
		info.controllerWindow = controllerWindow.getWindow()
		info.gamesInfoWindow = gameInfoWindow.getWindow()
		info.gamesListWindow = gamesWindow.getWindow()
		info.logWindow = logWindow.getWindow()
		info.outerWindow = Config.WindowInformation.Window().also {
			it.location = Vec2(window.pos.array.map { it.toFloat() })
			it.size = Vec2(window.size.array.map { it.toFloat() })
		}
		
		main.config.save()
	}
}