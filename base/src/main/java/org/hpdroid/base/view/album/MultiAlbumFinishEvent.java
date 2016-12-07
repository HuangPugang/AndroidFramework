package org.hpdroid.base.view.album;

import java.util.ArrayList;

/**
 * Created by luoliuqing on 16/10/27.
 */
public class MultiAlbumFinishEvent {
	ArrayList<String> list = new ArrayList<>();

	public MultiAlbumFinishEvent(ArrayList<String> list) {
		this.list = list;
	}

	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list) {
		this.list = list;
	}
}
