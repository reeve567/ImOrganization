package dev.reeve.organization.game

import dev.reeve.organization.Main
import dev.reeve.organization.game.info.GameFiles
import net.sf.image4j.codec.ico.ICOEncoder
import org.apache.commons.compress.utils.IOUtils
import org.ini4j.Wini
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import javax.imageio.ImageIO

class IconManager(private val main: Main) {
	fun removeIcons() {
	
	}
	
	fun updateIcons() {
	
	}
	
	fun setIcon(gameFiles: GameFiles) {
		val icon = File(gameFiles.location, "icon.ico")
		
		if (gameFiles.icon != null) {
			println("icon!")
			Files.copy(gameFiles.icon.toPath(), icon.toPath())
		} else {
			gameFiles.iconInputStreamData?.also {
				println("icon!")
				if (it.type == IconInputType.PNG) {
					val bufferedImage = ImageIO.read(it.iconInputStream)
					ICOEncoder.write(bufferedImage, icon)
				} else {
					var output: FileOutputStream? = icon.outputStream()
					IOUtils.copy(it.iconInputStream, output)
					output?.close()
					output = null
				}
				it.iconInputStream?.close()
				it.iconInputStream = null
			}
		}
		
		val ini = File(gameFiles.location, "desktop.ini")
		ini.delete()
		ini.createNewFile()
		val wini = Wini(ini)
		wini.put(".ShellClassInfo", "IconResource", "icon.ico,0")
		wini.put("ViewState", "Mode", "")
		wini.put("ViewState", "Vid", "")
		wini.put("ViewState", "FolderType", "Generic")
		wini.store()
		
		Runtime.getRuntime().exec("attrib +h +s ${ini.path}")
		Runtime.getRuntime().exec("attrib -h +s ${gameFiles.location!!.path}")
	}
}