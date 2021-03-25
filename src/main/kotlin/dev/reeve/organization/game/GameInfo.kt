package dev.reeve.organization.game

import com.github.kittinunf.fuel.Fuel
import com.google.gson.GsonBuilder
import dev.reeve.organization.Config
import dev.reeve.organization.game.info.ArtStyle
import dev.reeve.organization.game.info.EngineType
import dev.reeve.organization.gui.LogWindow
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import uno.kotlin.Quadruple
import java.awt.Image
import java.io.File
import java.util.logging.Level
import javax.imageio.ImageIO

class GameInfo(
	var name: String? = null,
	@Transient val modified: Long = 0,
	@Transient var dataFile: File? = null,
	var notes: String? = null,
	var playedLatest: Boolean = false,
	var walkthrough: String? = null,
	var url: String? = null,
	var artStyle: ArtStyle? = null,
	var engineType: EngineType? = null,
	var tags: List<String>? = null,
	var developmentCompleted: Boolean = false,
	var personallyCompleted: Boolean = false,
	var lastTitle: String? = null,
	var gameExecutable: String? = null
) {
	
	fun save() {
		if (dataFile != null) {
			val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
			val json = gson.toJson(this).replace("\\u0000","")
			dataFile!!.writeText(json)
		}
	}
	
	fun getFromURL(config: Config?, log: Boolean): Quadruple<EngineType, Boolean, List<String>, Image?>? {
		if (log)
			LogWindow.addLog("Trying to get data from web...")
		if (url != null && url != "") {
			try {
				if (log)
					LogWindow.addLog("Grabbing information...")
				
				val ret = Fuel.get(url!!)
				val doc = Jsoup.parse(ret.response().second.body().asString(null))
				
				fun elements(id: Int): Elements {
					return doc.body().getElementsByAttributeValue("href", "/forums/games.2/?prefix_id=$id")
				}
				
				var engine = EngineType.OTHER
				
				EngineType.values().forEach {
					if (elements(it.prefixID).isNotEmpty()) {
						engine = it
					}
				}
				var completed = false
				if (elements(18).isNotEmpty()) {
					completed = true
				}
				
				val list = doc.getElementsByClass("tagItem").map { element ->
					val r = element.text()
					if (config != null && !config.tags.contains(r)) {
						config.tags.add(r)
						config.tags.sort()
						config.save()
					}
					r
				}
				/*
				val imageElement = doc.body().getElementsByClass("cover-hasImage").firstOrNull()
				
				var url = imageElement?.let {
					var style = it.attr("style")
					style = style.substringAfterLast('(')
					style = style.substring(1)
					style = style.replaceAfter(')',"")
					style = style.substring(0, style.length - 1)
					style
				}
				
				if (imageElement == null) {
					LogWindow.addLog("null image -- trying method 2")
					doc.body().getElementsByClass("bbWrapper").first().getElementsByAttribute("target").first()?.also {
						url = it.attr("href")
					}
				}
				
				if (url == null) {
					val file = File(dataFile!!.parent, "index.html")
					Fuel.download(this.url!!).fileDestination { response, request ->
						file
					}.response()
					
					var output = ""
					file.inputStream().bufferedReader().lines().forEach {
						output += it
					}
					
					val newDoc = Jsoup.parse(output)
					url = newDoc.head().getElementsByAttributeValue("property","og:image").attr("content")
				}
				
				var image: Image? = null
				
				if (url != null) {
					LogWindow.addLog("Downloading image...")
					
					val file = File(dataFile!!.parentFile, "headerImage." + url!!.substringAfterLast('.'))
					LogWindow.addLog(file.canonicalPath)
					
					Fuel.download(url!!).fileDestination { response, request ->
						LogWindow.addLog(response.responseMessage)
						file
					}.response()
					
					image = ImageIO.read(getPicture())
				}
				*/
				
				return Quadruple(engine, completed, list, null)
			} catch (ignored: Exception) {
				LogWindow.addLog(ignored.stackTraceToString(), Level.SEVERE)
			}
		}
		return null
	}
	
	fun getPicture(): File? {
		val types = listOf("png", "jpg")
		for (type in types) {
			val file = File(dataFile!!.parent, "headerImage.$type")
			if (file.exists()) {
				return file
			}
		}
		return null
	}
}
