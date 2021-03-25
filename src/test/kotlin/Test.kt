import dev.reeve.organization.game.GameInfo
import dev.reeve.organization.game.info.EngineType
import org.junit.Test
import kotlin.test.assertFalse

class Test {
	private val outOfTouch = GameInfo("Out of Touch", 0, null, "", false, "", "https://f95zone.to/threads/out-of-touch-v1-24-5-beta-story-anon.67494/", null, null)
	private val luckyParadox = GameInfo("Lucky Paradox", 0, null, "", false, "", "https://f95zone.to/threads/33740/", null, null)
	
	@Test
	fun checkTags1() {
		val results = outOfTouch.getFromURL(null, false)
		assert(results!!.third.containsAll(listOf("3dcg","female protagonist", "harem", "humor", "male protagonist", "multiple protagonist", "paranormal", "romance", "school setting", "vaginal sex")))
	}
	
	@Test
	fun checkEngine1() {
		val results = outOfTouch.getFromURL(null, false)
		assert(results!!.first == EngineType.RENPY)
	}
	
	@Test
	fun checkCompleted1() {
		val results = outOfTouch.getFromURL(null, false)
		assertFalse(results!!.second)
	}
	
	@Test
	fun checkEngine2() {
		val results = luckyParadox.getFromURL(null, false)
		println(results)
		assert(results!!.first == EngineType.RENPY)
	}
}