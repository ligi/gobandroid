package org.ligi.gobandroid_hd.uitest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.ligi.gobandroid_hd.App
import org.ligi.gobandroid_hd.InteractionScope
import org.ligi.gobandroid_hd.R
import org.ligi.gobandroid_hd.logic.CellImpl
import org.ligi.gobandroid_hd.logic.GoGame
import org.ligi.gobandroid_hd.model.GameProvider
import org.ligi.gobandroid_hd.test_helper_functions.placeStone
import org.ligi.gobandroid_hd.ui.recording.GameRecordActivity
import org.ligi.trulesk.TruleskActivityRule
import org.ligi.trulesk.invokeMenu

class TheUndoFunctionality {

    @get:Rule
    val rule = TruleskActivityRule(GameRecordActivity::class.java)

    val gameProvider: GameProvider by App.kodein.lazy.instance()

    val interactionScope: InteractionScope by App.kodein.lazy.instance()

    @Before
    fun setUp() {
        gameProvider.set(GoGame(9))
    }

    @Test
    fun testThatAskVariantShowsWhenWanted() {

        interactionScope.ask_variant_session = true

        doAndUndo()

        rule.screenShot("ask_variant_dialog")
        onView(withText(R.string.keep_variant)).check(matches(isDisplayed()))
        assertThat(interactionScope.ask_variant_session).isTrue()
    }

    @Test
    fun testWeCanKeepVariation() {

        interactionScope.ask_variant_session = true

        doAndUndo()
        onView(withText(R.string.yes)).perform(click())

        assertThat(gameProvider.get().actMove.hasNextMove()).isTrue()
    }

    @Test
    fun testWeCanDiscardLastVariation() {

        interactionScope.ask_variant_session = true

        doAndUndo()
        onView(withText(R.string.no)).perform(click())

        assertThat(gameProvider.get().actMove.hasNextMove()).isFalse()
    }

    @Test
    fun testWeCanDisableVariationDialog() {

        interactionScope.ask_variant_session = true

        doAndUndo()
        onView(withText(R.string.no)).perform(click())
        onView(withText(R.string.snackbar_keep_variant_message_disable)).check(matches(isDisplayed()))
        onView(withText(R.string.snackbar_keep_variant_action_disable)).perform(click())

        assertThat(interactionScope.ask_variant_session).isFalse()
    }

    @Test
    fun testThatAskVariantDoesNotShowWhenNotWanted() {

        interactionScope.ask_variant_session = false

        doAndUndo()

        rule.screenShot("no_ask_variant_dialog")
        onView(withText(R.string.keep_variant)).check(doesNotExist())
        assertThat(interactionScope.ask_variant_session).isFalse()
        assertThat(gameProvider.get().actMove.hasNextMoveVariations()).isFalse()
    }

    @Test
    fun testWeCanEnableVariantDialogAgain() {

        interactionScope.ask_variant_session = false

        doAndUndo()
        onView(withText(R.string.snackbar_keep_variant_action_enable)).perform(click())

        assertThat(interactionScope.ask_variant_session).isTrue()
    }

    @Test
    fun testWeCanUndoPass() {

        interactionScope.ask_variant_session = false

        gameProvider.get().pass()

        invokeMenu(R.id.menu_game_undo, R.string.undo)
        onView(withId(R.id.go_board)).perform(placeStone(CellImpl(1, 1), gameProvider.get()))
    }

    private fun doAndUndo() {
        onView(withId(R.id.go_board)).perform(placeStone(CellImpl(1, 1), gameProvider.get()))

        invokeMenu(R.id.menu_game_undo, R.string.undo)
    }

}
