package Classes;

public class Query {
	//you can modify this class

	private String queryContent;	
	private String topicId;	
	private String queryTitle;
	private String queryDesc;
	private String queryNarr;
	
	public void SetQueryContent(String content){
		queryContent = content;
	}	
	public String GetQueryContent() {
		return queryContent;
	}
	public void SetTopicId(String id){
		topicId = id;
	}
	public String GetTopicId() {
		return topicId;
	}
	
	public void setQueryTitle(String title){
		queryTitle = title;
	}	
	public String getQueryTitle(){
		return queryTitle;
	}
	public void setQueryDesc(String desc){
		queryDesc = desc;
	}	
	public String getQueryDesc(){
		return queryDesc;
	}
	public void setQueryNarr(String narr){
		queryNarr = narr;
	}	
	public String getQueryNarr(){
		return queryNarr;
	}
}
