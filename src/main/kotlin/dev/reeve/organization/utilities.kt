package dev.reeve.organization

import imgui.TextureID
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import java.awt.image.BufferedImage
import java.io.*
import java.nio.ByteBuffer
import java.util.*
import java.util.regex.Pattern

fun getVersion(string: String): String {
	if (string.isEmpty()) {
		println("Empty string")
		return "???"
	}
	if (!string.any {
			it.isDigit()
		}) return "???"
	val pattern = Pattern.compile("([A-Za-z])?[0-9]+[.]([A-Za-z0-9]+)?.?([A-Za-z0-9]+)?.?([A-Za-z0-9]+)?")
	val matcher = pattern.matcher(string)
	
	if (matcher.find()) {
		return string.substring(matcher.start(), matcher.end())
	}
	
	return "???"
}

fun convertName(string: String, config: Config): String {
	if (string.isEmpty()) {
		println("Empty string")
		return "???"
	} else {
		return string
			.replace("/Game.exe", "")
			.split('/').last()
			.replace(".exe", "")
			.replace("'", "")
			.replace("-", "_")
			.replace(" ", "_")
			.split("_").filter {
				for (str in config.excludedClips) {
					if (it.contains(str, true)) {
						return@filter false
					}
				}
				return@filter true
			}.joinToString(separator = "") {
				if (it.isNotEmpty()) {
					it.first().toUpperCase() + it.substring(1)
				}
				it
			}.let { s ->
				if (s.contains('.')) {
					return@let s.split('.').first().let {
						var temp = it
						val pattern = Pattern.compile("[0-9]|[Vv]")
						while (pattern.matcher(temp.substring(temp.lastIndex)).find()) {
							temp = temp.substring(0, temp.lastIndex)
						}
						temp
					}
				}
				return@let s
			}.let {
				if (config.conversions.containsKey(it)) {
					config.conversions[it] ?: error("how did you fuck this up")
				}
				it
			}
		
	}
}

fun Boolean.ifRun(block: () -> Unit): Boolean {
	if (this) {
		block.invoke()
	}
	return this
}

fun runPowershellCommand(command: String): Process {
	return Runtime.getRuntime().exec("powershell.exe $command").also {
		it.waitFor()
	}
}

fun loadTexture(image: BufferedImage): TextureID {
	val pixels = IntArray(image.width * image.height)
	image.getRGB(0,0, image.width, image.height,pixels,0, image.width)
	val buffer = BufferUtils.createByteBuffer(image.width * image.height * 4)
	
	for (y in 0 until image.height) {
		for (x in 0 until image.width) {
			val pixel = pixels[y * image.width + x]
			buffer.put(pixel.shr(16).and(0xFF).toByte()) // R
			buffer.put(pixel.shr(8).and(0xFF).toByte()) // G
			buffer.put(pixel.and(0xFF).toByte()) // B
			buffer.put(pixel.shr(24).and(0xFF).toByte()) // A
		}
	}
	buffer.flip()
	
	val id: TextureID = glGenTextures()
	glBindTexture(GL_TEXTURE_2D, id)
	
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
	
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
	
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.width, image.height, 0 , GL_RGBA, GL_UNSIGNED_BYTE, buffer)
	
	return id
}