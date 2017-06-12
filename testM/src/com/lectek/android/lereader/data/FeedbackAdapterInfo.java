package com.lectek.android.lereader.data;

public class FeedbackAdapterInfo {
	
		private String  content;
		private String  createTime;
		private int fromTag;//标识客户端或是服务端
		private String contentType;
		
		private Integer feedbackId;
		private String  closeTag;//是否关闭的话题
		private Integer score;
		
		public Integer getFeedbackId() {
			return feedbackId;
		}
		public void setFeedbackId(Integer feedbackId) {
			this.feedbackId = feedbackId;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getCloseTag() {
			return closeTag;
		}
		public void setCloseTag(String closeTag) {
			this.closeTag = closeTag;
		}
		public int getFromTag() {
			return fromTag;
		}
		public void setFromTag(int fromTag) {
			this.fromTag = fromTag;
		}
		public String getContentType() {
			return contentType;
		}
		public void setContentType(String contentType) {
			this.contentType = contentType;
		}
		public Integer getScore() {
			return score;
		}
		public void setScore(Integer score) {
			this.score = score;
		}
		

}
