package dev.reeve.organization.gui

import dev.reeve.organization.Config
import dev.reeve.organization.game.GameInfo
import dev.reeve.organization.game.GameManager
import dev.reeve.organization.game.info.ArtStyle
import dev.reeve.organization.game.info.EngineType
import dev.reeve.organization.ifRun
import glm_.vec2.Vec2
import imgui.*
import imgui.dsl.menu
import imgui.dsl.menuBar
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.jvm.javaSetter

class GamesWindow(
	private val gameManager: GameManager,
	window: Config.WindowInformation.GamesListWindow,
) : ImGuiWindow("Games", ControllerWindow::showGames, window) {
	
	private var filterType = FilterType.NONE
	private var nameFilter: String? = null
	private var engineFilter: EngineType? = null
	private var artFilter: ArtStyle? = null
	private var urlFilter: TwoChoice = TwoChoice.NEITHER
	private var developmentCompletedFilter = TwoChoice.NEITHER
	private var personallyCompletedFilter = TwoChoice.NEITHER
	private var playedLatestFilter = TwoChoice.NEITHER
	
	private var sortBy = window.sortType
	private var sortInverted = window.inverted
	
	private var selected = -1
	var selecting = false
	var selectedGame: GameInfo? = null
	
	private enum class FilterType(val field: KMutableProperty1<GamesWindow, *>?) {
		NAME(GamesWindow::nameFilter),
		TAG(null),
		ART(GamesWindow::artFilter),
		ENGINE(GamesWindow::engineFilter),
		DEVELOPMENT_COMPLETED(GamesWindow::developmentCompletedFilter),
		PERSONALLY_COMPLETED(GamesWindow::personallyCompletedFilter),
		URL(GamesWindow::urlFilter),
		PLAYED(GamesWindow::playedLatestFilter),
		
		NONE(null)
	}
	
	enum class SortType {
		NAME,
		DATE,
		DEFAULT
	}
	
	private enum class TwoChoice {
		YES,
		NO,
		NEITHER
	}
	
	override fun setFlags(): WindowFlags {
		return WindowFlag.MenuBar.i
	}
	
	override fun preInvoke(imGui: ImGui) {
		imGui.setNextWindowSize(Vec2(800, 600), Cond.FirstUseEver)
	}
	
	override fun invoke(imGui: ImGui) {
		with(imGui) {
			menuBar {
				menu("Filter") {
					menu("By Engine") {
						EngineType.values().forEach {
							menuItem(it.displayLabel, selected = it == engineFilter).ifRun {
								engineFilter = it
								filterType = FilterType.ENGINE
								resetMenuItems(FilterType.ENGINE)
							}
						}
					}
					menu("By Art") {
						ArtStyle.values().forEach {
							menuItem(it.displayLabel, selected = it == artFilter).ifRun {
								artFilter = it
								filterType = FilterType.ART
								resetMenuItems(FilterType.ART)
							}
						}
					}
					menu("Has URL") {
						listOf(TwoChoice.YES, TwoChoice.NO).forEach {
							menuItem(it.toString().toLowerCase(), selected = it == urlFilter).ifRun {
								urlFilter = it
								filterType = FilterType.URL
								resetMenuItems(FilterType.URL)
							}
						}
					}
					menu("Have Played") {
						listOf(TwoChoice.YES, TwoChoice.NO).forEach {
							menuItem(it.toString().toLowerCase(), selected = it == playedLatestFilter).ifRun {
								playedLatestFilter = it
								filterType = FilterType.PLAYED
								resetMenuItems(FilterType.PLAYED)
							}
						}
					}
					menu("Development Completed") {
						listOf(TwoChoice.YES, TwoChoice.NO).forEach {
							menuItem(it.toString().toLowerCase(), selected = it == developmentCompletedFilter).ifRun {
								developmentCompletedFilter = it
								filterType = FilterType.DEVELOPMENT_COMPLETED
								resetMenuItems(FilterType.DEVELOPMENT_COMPLETED)
							}
						}
					}
					menu("Personally Completed") {
						listOf(TwoChoice.YES, TwoChoice.NO).forEach {
							menuItem(it.toString().toLowerCase(), selected = it == personallyCompletedFilter).ifRun {
								personallyCompletedFilter = it
								filterType = FilterType.PERSONALLY_COMPLETED
								resetMenuItems(FilterType.PERSONALLY_COMPLETED)
							}
						}
					}
					menuItem("Remove").ifRun {
						filterType = FilterType.NONE
						resetMenuItems(FilterType.NONE)
					}
				}
				menu("Sort") {
					menuItem("Name", selected = SortType.NAME == sortBy).ifRun {
						sortBy = SortType.NAME
						selected = -1
						selectedGame = null
					}
					menuItem("Date", selected = SortType.DATE == sortBy).ifRun {
						sortBy = SortType.DATE
						selected = -1
						selectedGame = null
					}
					menuItem("Inverted", selected = ::sortInverted).ifRun {
						selected = -1
						selectedGame = null
					}
					menuItem("Remove").ifRun {
						sortBy = SortType.DEFAULT
						sortInverted = false
						selected = -1
						selectedGame = null
					}
				}
			}
			
			if (gameManager.isAvailable())
				text("${gameManager.games.size} Games:")
			columns(9, "games")
			separator()
			text("Name"); nextColumn()
			text("Played"); nextColumn()
			text("Art"); nextColumn()
			text("Engine"); nextColumn()
			text("Tags"); nextColumn()
			text("URL"); nextColumn()
			text("Dev. Completed"); nextColumn()
			text("Pers. Completed"); nextColumn()
			text("Date"); nextColumn()
			separator()
			
			if (gameManager.isAvailable()) {
				
				var games = gameManager.games
				
				if (filterType != FilterType.NONE) {
					games = when (filterType) {
						FilterType.ENGINE -> {
							games.filter { it.engineType == engineFilter }
						}
						FilterType.ART -> {
							games.filter { it.artStyle == artFilter }
						}
						FilterType.URL -> {
							games.filter { it.url != null && it.url != "" }
						}
						FilterType.NAME -> {
							games.filter { it.name != null }
						}
						FilterType.TAG -> {
							games.filter { it.tags != null && it.tags!!.isNotEmpty() }
						}
						FilterType.DEVELOPMENT_COMPLETED -> {
							games.filter { (if (it.developmentCompleted) 0 else 1) == developmentCompletedFilter.ordinal }
						}
						FilterType.PERSONALLY_COMPLETED -> {
							games.filter { (if (it.personallyCompleted) 0 else 1) == personallyCompletedFilter.ordinal }
						}
						FilterType.PLAYED -> {
							games.filter { (if (it.playedLatest) 0 else 1) == playedLatestFilter.ordinal }
						}
						FilterType.NONE -> {
							error("how did this happen")
						}
					}
				}
				
				if (sortBy != SortType.DEFAULT) {
					games = when (sortBy) {
						SortType.NAME -> {
							if (sortInverted) {
								games.sortedByDescending {
									it.name!!.toUpperCase()
								}
							} else {
								games.sortedBy {
									it.name!!.toUpperCase()
								}
							}
						}
						SortType.DATE -> {
							if (sortInverted) {
								games.sortedBy {
									it.modified
								}
							} else {
								games.sortedByDescending {
									it.modified
								}
							}
						}
						SortType.DEFAULT -> {
							error("how did this happen")
						}
					}
				}
				
				val items = games.size
				val names = games.map { it.name }
				val played = games.map { if (it.playedLatest) "yes" else "no" }
				val art = games.map { it.artStyle?.displayLabel ?: "" }
				val engine = games.map { it.engineType?.displayLabel ?: "" }
				val tags = games.map { it.tags?.size ?: "?" }
				val url = games.map { if (it.url != null && it.url != "") "yes" else "no" }
				val developmentCompleted = games.map { if (it.developmentCompleted) "yes" else "no" }
				val personallyCompleted = games.map { if (it.personallyCompleted) "yes" else "no" }
				val date = games.map { it.modified }
				for (i in 0 until items) {
					selectable(names[i]!!, selected == i, SelectableFlag.SpanAllColumns.i); nextColumn()
					
					if (isItemClicked()) {
						selecting = true
						selected = i
						if (selectedGame == null) {
							selectedGame = games[i]
						} else {
							synchronized(selectedGame!!) {
								selectedGame = games[i]
							}
						}
						selecting = false
					}
					
					text(played[i]); nextColumn()
					text(art[i]); nextColumn()
					text(engine[i]); nextColumn()
					text(tags[i].toString()); nextColumn()
					text(url[i]); nextColumn()
					text(developmentCompleted[i]); nextColumn()
					text(personallyCompleted[i]); nextColumn()
					text(date[i].toString()); nextColumn()
				}
			}
			
			columns(1)
		}
	}
	
	private fun resetMenuItems(otherThan: FilterType) {
		FilterType.values().forEach {
			if (it != otherThan) {
				if (it.field?.get(this) is TwoChoice) {
					it.field.javaSetter?.invoke(this, TwoChoice.NEITHER)
				} else {
					it.field?.javaSetter?.invoke(this, null)
				}
			}
		}
	}
	
	override fun getWindow(): Config.WindowInformation.GamesListWindow {
		val window = super.getWindow()
		val new = Config.WindowInformation.GamesListWindow()
		new.location = window.location
		new.size = window.size
		new.sortType = sortBy
		new.inverted = sortInverted
		return new
	}
}