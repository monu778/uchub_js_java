package com.broadsoft.demohub.api.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MailData extends BaseResponse{

	private String auth;
	private String id;
	private String folder;
	private String folder_id;
	private String action;
	private String session;
	private Boolean attachment;
	private String[] from;
	private String[] to;
	private String[] cc;
	private String[] bcc;
	private String subject;
	private Number size;
	private String sent_date;
	private String recieved_date;
	private Number priority;
	private String msg_ref;
	private String flag_seen;
	private String account_name;
	private Integer account_id;
	private String[] user;
	private Object headers;
	private String[] attachments;
	private String[] nested_msgs;
	private Boolean truncated;
	private String source;
	private String cid;
	private String original_id;
	private String original_folder_id;
	private Integer data;
	private String columns;
	private String sort;
	private String order;
	private String timestamp;
	private String unseen;
	private String leftHandLimit;
	private String rightHandLimit;
	private String save;
	private String attachment_id;
	private String seen;
	private String searchString;
	private String operator;
	private String field;
	private String value;
	private Boolean isInvite;
	private String message;
	private String sequenceId;
	private Boolean onlyAttachments;
	private String view;
	private String search;
	
	
	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public Boolean getOnlyAttachments() {
		return onlyAttachments;
	}

	public void setOnlyAttachments(Boolean onlyAttachments) {
		this.onlyAttachments = onlyAttachments;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean isInvite() {
		return isInvite;
	}

	public void setInvite(Boolean isInvite) {
		this.isInvite = isInvite;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSeen() {
		return seen;
	}

	public void setSeen(String seen) {
		this.seen = seen;
	}

	public String getSave() {
		return save;
	}

	public void setSave(String save) {
		this.save = save;
	}

	public String getAttachment_id() {
		return attachment_id;
	}

	public void setAttachment_id(String attachment_id) {
		this.attachment_id = attachment_id;
	}

	public String getLeftHandLimit() {
		return leftHandLimit;
	}

	public void setLeftHandLimit(String leftHandLimit) {
		this.leftHandLimit = leftHandLimit;
	}

	public String getRightHandLimit() {
		return rightHandLimit;
	}

	public void setRightHandLimit(String rightHandLimit) {
		this.rightHandLimit = rightHandLimit;
	}

	public String getUnseen() {
		return unseen;
	}

	public void setUnseen(String unseen) {
		this.unseen = unseen;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public enum flags{
		answered(1),deleted(2),draft(4),flagged(8), recent(16), seen(32), user(64), spam (128), forwarded(256);
		private int flagValue;
		private flags(int flagValue){
			this.flagValue=flagValue;
		}
		public int getFlagValue()
		{
			return flagValue;
		}
	}
	
	public enum level{
		noPriority(0), veryLow(5) , low(4), normal(3), high(2), veryHigh(1);
		private int level;
		private level(int level){
			this.level=level;
		}
		public int getLevelValue()
		{
			return level;
		}
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getAttachment() {
		return attachment;
	}

	public void setAttachment(Boolean attachment) {
		this.attachment = attachment;
	}

	public String[] getFrom() {
		return from;
	}

	public void setFrom(String[] from) {
		this.from = from;
	}

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public String[] getCc() {
		return cc;
	}

	public void setCc(String[] cc) {
		this.cc = cc;
	}

	public String[] getBcc() {
		return bcc;
	}

	public void setBcc(String[] bcc) {
		this.bcc = bcc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Number getSize() {
		return size;
	}

	public void setSize(Number size) {
		this.size = size;
	}

	public String getSent_date() {
		return sent_date;
	}

	public void setSent_date(String sent_date) {
		this.sent_date = sent_date;
	}

	public String getRecieved_date() {
		return recieved_date;
	}

	public void setRecieved_date(String recieved_date) {
		this.recieved_date = recieved_date;
	}


	public Number getPriority() {
		return priority;
	}

	public void setPriority(Number priority) {
		this.priority = priority;
	}

	public String getMsg_ref() {
		return msg_ref;
	}

	public void setMsg_ref(String msg_ref) {
		this.msg_ref = msg_ref;
	}

	public String getFlag_seen() {
		return flag_seen;
	}

	public void setFlag_seen(String flag_seen) {
		this.flag_seen = flag_seen;
	}

	public String getAccount_name() {
		return account_name;
	}

	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}

	public Integer getAccount_id() {
		return account_id;
	}

	public void setAccount_id(Integer account_id) {
		this.account_id = account_id;
	}

	public String[] getUser() {
		return user;
	}

	public void setUser(String[] user) {
		this.user = user;
	}

	public Object getHeaders() {
		return headers;
	}

	public void setHeaders(Object headers) {
		this.headers = headers;
	}

	public String[] getAttachments() {
		return attachments;
	}

	public void setAttachments(String[] attachments) {
		this.attachments = attachments;
	}

	public String[] getNested_msgs() {
		return nested_msgs;
	}

	public void setNested_msgs(String[] nested_msgs) {
		this.nested_msgs = nested_msgs;
	}

	public Boolean getTruncated() {
		return truncated;
	}

	public void setTruncated(Boolean truncated) {
		this.truncated = truncated;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getOriginal_id() {
		return original_id;
	}

	public void setOriginal_id(String original_id) {
		this.original_id = original_id;
	}

	public String getOriginal_folder_id() {
		return original_folder_id;
	}

	public void setOriginal_folder_id(String original_folder_id) {
		this.original_folder_id = original_folder_id;
	}

	public Integer getData() {
		return data;
	}

	public void setData(Integer data) {
		this.data = data;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getFolder_id() {
		return folder_id;
	}

	public void setFolder_id(String folder_id) {
		this.folder_id = folder_id;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	
}
