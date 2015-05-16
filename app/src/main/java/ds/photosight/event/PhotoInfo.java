package ds.photosight.event;

import java.util.List;
import java.util.Map;

public class PhotoInfo {

	public int tab;
	public int category;
	public int page;
	public int item;
	public List<Map<Integer, String>> data;


	public PhotoInfo(final int tab, final int category, final int page, final int item) {
		this(tab, category, page, item, null);
	}


	public PhotoInfo(final int tab, final int category, final int page, final int item, List<Map<Integer, String>> data) {
		this.tab = tab;
		this.category = category;
		this.page = page;
		this.item = item;
		this.data = data;
	}
}
