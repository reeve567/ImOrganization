package dev.reeve.organization

import com.google.gson.*
import glm_.vec2.Vec2
import java.lang.reflect.Type

class Vec2Handler: JsonSerializer<Vec2>, JsonDeserializer<Vec2> {
	override fun serialize(src: Vec2, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
		val element = JsonObject()
		element.add("x", JsonPrimitive(src.x))
		element.add("y", JsonPrimitive(src.y))
		return element
	}
	
	override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Vec2 {
		return Vec2(json.asJsonObject.getAsJsonPrimitive("x").asFloat, json.asJsonObject.getAsJsonPrimitive("y").asFloat).also {
			println(it)
		}
	}
}