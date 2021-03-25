package dev.reeve.organization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.reeve.organization.gui.GamesWindow
import glm_.vec2.Vec2
import java.io.File

class Config {
	companion object {
		val gson: Gson = GsonBuilder()
			.registerTypeAdapter(Vec2::class.java, Vec2Handler())
			.disableHtmlEscaping()
			.setPrettyPrinting()
			.create()
	}
	
	fun save() {
		dataLocation.writeText(gson.toJson(this))
	}
	
	@Transient
	lateinit var dataLocation: File
	var saveWindows = false
	var conversions = emptyMap<String, String>()
	var excludedClips = emptyList<String>()
	var excludedExe = emptyList<String>()
	var tags = mutableListOf<String>()
	var windowInfo = WindowInformation()
	
	class WindowInformation {
		open class Window {
			var location = Vec2()
			var size = Vec2()
		}
		
		class GamesListWindow : Window() {
			var sortType = GamesWindow.SortType.DEFAULT
			var inverted = false
		}
		
		var controllerWindow = Window()
		var logWindow = Window()
		var gamesListWindow = GamesListWindow()
		var gamesInfoWindow = Window()
		var outerWindow = Window()
	}
}