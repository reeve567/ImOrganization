package dev.reeve.organization.gui

import dev.reeve.organization.Config
import glm_.vec2.Vec2
import imgui.Cond
import imgui.ImGui
import imgui.WindowFlags
import imgui.dsl.window
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

abstract class ImGuiWindow(private val title: String, private val visible: KMutableProperty0<Boolean>?, private val window: Config.WindowInformation.Window) {
	private val flags: WindowFlags
	var location = Vec2()
	var size = Vec2()
	
	init {
		flags = setFlags()
	}
	
	abstract fun setFlags(): WindowFlags
	fun run(imGui: ImGui) {
		if (visible == null || visible.get()) {
			preInvoke(imGui)
			
			if (!window.location.array.all { it == 0f }) {
				imGui.setNextWindowPos(window.location, Cond.FirstUseEver)
			}
			
			if (!window.size.array.all { it == 0f }) {
				imGui.setNextWindowSize(window.size, Cond.FirstUseEver)
			}
			
			window(title, null, flags) {
				location = imGui.windowPos
				size = imGui.windowSize
				
				invoke(imGui)
			}
		}
	}
	abstract fun invoke(imGui: ImGui)
	open fun preInvoke(imGui: ImGui) {}
	
	open fun getWindow(): Config.WindowInformation.Window {
		return Config.WindowInformation.Window().also {
			if (!location.array.all { it == 0f }) {
				it.location = location
			} else {
				it.location = window.location
			}
			
			if (!size.array.all { it == 0f }) {
				it.size = size
			} else {
				it.size = window.size
			}
		}
	}
}
