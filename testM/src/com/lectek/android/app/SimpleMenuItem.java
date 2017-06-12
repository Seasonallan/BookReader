package com.lectek.android.app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

public class SimpleMenuItem implements MenuItem {
	private int mId;
	public SimpleMenuItem(int id) {
		mId = id;
	}

	@Override
	public int getItemId() {
		return mId;
	}

	@Override
	public int getGroupId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MenuItem setTitle(CharSequence title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MenuItem setTitle(int title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharSequence getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MenuItem setTitleCondensed(CharSequence title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharSequence getTitleCondensed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MenuItem setIcon(Drawable icon) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MenuItem setIcon(int iconRes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Drawable getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MenuItem setIntent(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Intent getIntent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MenuItem setShortcut(char numericChar, char alphaChar) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MenuItem setNumericShortcut(char numericChar) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char getNumericShortcut() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MenuItem setAlphabeticShortcut(char alphaChar) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char getAlphabeticShortcut() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MenuItem setCheckable(boolean checkable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCheckable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MenuItem setChecked(boolean checked) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MenuItem setVisible(boolean visible) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MenuItem setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSubMenu() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SubMenu getSubMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MenuItem setOnMenuItemClickListener(
			OnMenuItemClickListener menuItemClickListener) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContextMenuInfo getMenuInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean collapseActionView() {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean expandActionView() {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public ActionProvider getActionProvider() {
		return null;
		// TODO Auto-generated method stub
		
	}

	@Override
	public View getActionView() {
		return null;
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isActionViewExpanded() {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public MenuItem setActionProvider(ActionProvider actionProvider) {
		return null;
		// TODO Auto-generated method stub
		
	}

	@Override
	public MenuItem setActionView(View view) {
		return null;
		// TODO Auto-generated method stub
		
	}

	@Override
	public MenuItem setActionView(int resId) {
		return null;
		// TODO Auto-generated method stub
		
	}

	@Override
	public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
		return null;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShowAsAction(int actionEnum) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MenuItem setShowAsActionFlags(int actionEnum) {
		return null;
		// TODO Auto-generated method stub
		
	}

}
