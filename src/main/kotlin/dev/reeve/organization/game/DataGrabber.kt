package dev.reeve.organization.game

import com.google.gson.GsonBuilder
import dev.reeve.organization.Config
import dev.reeve.organization.Main
import java.io.File

class DataGrabber(private val main: Main) {
	fun getGameNotes(filters: List<String>): List<GameInfo> {
		val list = mutableListOf<GameInfo>()
		for (game in main.games.listFiles()) {
			if (game.isDirectory) {
				val dataFile = File(game, "data.json")
				
				val gameNotes = when {
					dataFile.exists() -> {
						val gameNotesGson = Config.gson.fromJson(dataFile.readText(), GameInfo::class.java)
						val notes = GameInfo(
							game.name,
							game.lastModified(),
							dataFile,
							gameNotesGson.notes,
							gameNotesGson.playedLatest,
							gameNotesGson.walkthrough,
							gameNotesGson.url,
							gameNotesGson.artStyle,
							gameNotesGson.engineType,
							gameNotesGson.tags
						)
						if (filters.isNotEmpty()) {
							if (notes.tags != null && notes.tags!!.containsAll(filters.toList()))
								notes
							else
								null
						} else {
							notes
						}
						
					}
					filters.isEmpty() -> {
						GameInfo(game.name, game.lastModified(), dataFile)
					}
					else -> {
						null
					}
				}
				
				if (gameNotes != null)
					list.add(gameNotes)
			}
		}
		
		return list
	}
}