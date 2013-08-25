package org.ligi.gobandroid_hd.ui.application;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.SubMenu;
import android.view.View;

/**
 * we need that to craft a non ABS home MenuItem from home ABS MenuItem
 * Created by ligi on 6/1/13.
 */
class HomeMenuItemForNavigationDrawer implements android.view.MenuItem {

    @Override
    public int getItemId() {
        return android.R.id.home;
    }

    @Override
    public int getGroupId() {
        return 0;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public android.view.MenuItem setTitle(CharSequence charSequence) {
        return null;
    }

    @Override
    public android.view.MenuItem setTitle(int i) {
        return null;
    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

    @Override
    public android.view.MenuItem setTitleCondensed(CharSequence charSequence) {
        return null;
    }

    @Override
    public CharSequence getTitleCondensed() {
        return null;
    }

    @Override
    public android.view.MenuItem setIcon(Drawable drawable) {
        return null;
    }

    @Override
    public android.view.MenuItem setIcon(int i) {
        return null;
    }

    @Override
    public Drawable getIcon() {
        return null;
    }

    @Override
    public android.view.MenuItem setIntent(Intent intent) {
        return null;
    }

    @Override
    public Intent getIntent() {
        return null;
    }

    @Override
    public android.view.MenuItem setShortcut(char c, char c2) {
        return null;
    }

    @Override
    public android.view.MenuItem setNumericShortcut(char c) {
        return null;
    }

    @Override
    public char getNumericShortcut() {
        return 0;
    }

    @Override
    public android.view.MenuItem setAlphabeticShortcut(char c) {
        return null;
    }

    @Override
    public char getAlphabeticShortcut() {
        return 0;
    }

    @Override
    public android.view.MenuItem setCheckable(boolean b) {
        return null;
    }

    @Override
    public boolean isCheckable() {
        return false;
    }

    @Override
    public android.view.MenuItem setChecked(boolean b) {
        return null;
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public android.view.MenuItem setVisible(boolean b) {
        return null;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public android.view.MenuItem setEnabled(boolean b) {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean hasSubMenu() {
        return false;
    }

    @Override
    public SubMenu getSubMenu() {
        return null;
    }

    @Override
    public android.view.MenuItem setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        return null;
    }

    @Override
    public ContextMenu.ContextMenuInfo getMenuInfo() {
        return null;
    }

    @Override
    public void setShowAsAction(int i) {

    }

    @Override
    public android.view.MenuItem setShowAsActionFlags(int i) {
        return null;
    }

    @Override
    public android.view.MenuItem setActionView(View view) {
        return null;
    }

    @Override
    public android.view.MenuItem setActionView(int i) {
        return null;
    }

    @Override
    public View getActionView() {
        return null;
    }

    @Override
    public android.view.MenuItem setActionProvider(ActionProvider actionProvider) {
        return null;
    }

    @Override
    public ActionProvider getActionProvider() {
        return null;
    }

    @Override
    public boolean expandActionView() {
        return false;
    }

    @Override
    public boolean collapseActionView() {
        return false;
    }

    @Override
    public boolean isActionViewExpanded() {
        return false;
    }

    @Override
    public android.view.MenuItem setOnActionExpandListener(OnActionExpandListener onActionExpandListener) {
        return null;
    }
}
