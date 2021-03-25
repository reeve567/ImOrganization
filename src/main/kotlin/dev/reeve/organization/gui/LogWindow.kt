package dev.reeve.organization.gui

import dev.reeve.organization.Config
import dev.reeve.organization.ifRun
import glm_.vec2.Vec2
import imgui.ImGui
import imgui.StyleVar
import imgui.WindowFlag
import imgui.WindowFlags
import imgui.classes.TextFilter
import imgui.dsl.child
import imgui.dsl.popup
import java.util.*
import java.util.logging.Level
import kotlin.collections.ArrayList

class LogWindow(window: Config.WindowInformation.Window) : ImGuiWindow("Log", ControllerWindow::showLog, window) {
	
	companion object {
		val backLog = mutableListOf<Pair<String, Level>>()
		
		fun addLog(string: String, logLevel: Level = Level.INFO) {
			backLog.add(string to logLevel)
		}
	}
	
	private val buf = StringBuilder()
	private val filter = TextFilter()
	
	/** Index to lines offset. We maintain this with AddLog() calls. */
	private val lineOffsets = ArrayList<Int>()
	
	/** Keep scrolling if already at the bottom. */
	private var autoScroll = true
	
	init {
		clear()
	}
	
	override fun setFlags(): WindowFlags {
		return 0
	}
	
	override fun invoke(imGui: ImGui) {
		with(imGui) {
			// Options menu
			popup("Options") {
				checkbox("Auto-scroll", ::autoScroll)
			}
			
			// Main window
			button("Options").ifRun {
				openPopup("Options")
			}
			sameLine()
			val clear = button("Clear")
			sameLine()
			val copy = button("Copy")
			sameLine()
			filter.draw("Filter", -100f)
			
			separator()
			
			child("scrorlling", Vec2(0, 0), false, WindowFlag.HorizontalScrollbar.i) {
				if (clear) clear()
				if (copy) logToClipboard()
				
				pushStyleVar(StyleVar.ItemSpacing, Vec2(0))
				
				if (backLog.isNotEmpty()) {
					addLog(backLog.first().first, backLog.first().second)
					backLog.removeAt(0)
				}
				
				if (filter.isActive()) {
					// In this example we don't use the clipper when Filter is enabled.
					// This is because we don't have a random access on the result on our filter.
					// A real application processing logs with ten of thousands of entries may want to store the result of
					// search/filter.. especially if the filtering function is not trivial (e.g. reg-exp).
					for (line_no in 0 until lineOffsets.size) {
						val line = buf.subSequence(lineOffsets[line_no],
							if (line_no + 1 < lineOffsets.size) lineOffsets[line_no + 1] - 1 else buf.length).toString()
						if (filter.passFilter(line))
							textEx(line)
					}
				}
				else {
					// The simplest and easy way to display the entire buffer:
					//   ImGui::TextUnformatted(buf_begin, buf_end);
					// And it'll just work. TextUnformatted() has specialization for large blob of text and will fast-forward
					// to skip non-visible lines. Here we instead demonstrate using the clipper to only process lines that are
					// within the visible area.
					// If you have tens of thousands of items and their processing cost is non-negligible, coarse clipping them
					// on your side is recommended. Using ImGuiListClipper requires
					// - A) random access into your data
					// - B) items all being the  same height,
					// both of which we can handle since we an array pointing to the beginning of each line of text.
					// When using the filter (in the block of code above) we don't have random access into the data to display
					// anymore, which is why we don't use the clipper. Storing or skimming through the search result would make
					// it possible (and would be recommended if you want to search through tens of thousands of entries).
					for (line_no in 0 until lineOffsets.size) {
						val line = buf.subSequence(lineOffsets[line_no],
							if (line_no + 1 < lineOffsets.size) lineOffsets[line_no + 1] - 1 else buf.length).toString()
						textEx(line)
					}
				}
				
				popStyleVar()
				
				if (autoScroll && scrollY >= scrollMaxY)
					setScrollHereY(1f)
				
			}
			
		}
	}
	
	fun addLog(fmt: String, logLevel: Level = Level.INFO) {
		val oldSize = buf.length
		
		buf.append("[${logLevel.localizedName}] [${Date().hours}:${Date().minutes}:${Date().seconds}] $fmt${if (!fmt.endsWith('\n')) "\n" else ""}")
		
		for (i in oldSize until buf.length) {
			if (buf[i] == '\n')
				lineOffsets += i + 1
		}
	}
	
	fun clear() {
		buf.setLength(0)
		lineOffsets.clear()
		lineOffsets += 0
	}
}