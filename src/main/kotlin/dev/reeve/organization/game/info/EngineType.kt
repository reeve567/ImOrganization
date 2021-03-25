package dev.reeve.organization.game.info

enum class EngineType(val displayLabel: String, val prefixID: Int = -1) {
	RENPY("Renpy", 7),
	RPGM("RPGM", 2),
	UNITY("Unity", 3),
	UNREAL("Unreal", 31),
	HTML("HTML", 4),
	OTHER("Unknown/Other")
}