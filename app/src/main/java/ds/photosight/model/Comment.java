package ds.photosight.model;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable{

	public String content;
	public String name;
	public String status;
	public String avatarUrl;
	public String rate;
	public String dateRaw;
	public String dateFormatted;
	public Date date;

}
