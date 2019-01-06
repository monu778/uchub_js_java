package com.broadsoft.demohub.api.manager;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.broadsoft.demohub.api.beans.AllMailResponse;
import com.broadsoft.demohub.api.beans.Attachment;
import com.broadsoft.demohub.api.beans.BaseResponse;
import com.broadsoft.demohub.api.beans.FolderData;
import com.broadsoft.demohub.api.beans.InviteResponse;
import com.broadsoft.demohub.api.beans.MailData;
import com.broadsoft.demohub.api.beans.MailDelete;
import com.broadsoft.demohub.api.beans.MailResponse;
import com.broadsoft.demohub.api.beans.UpdateMail;
import com.broadsoft.demohub.api.beans.UserParticipant;

@Component
public interface MailManagerIF {

	public MailData  getCount(MailData mailData) throws Exception;
	public ArrayList<AllMailResponse> getAllMails(MailData mailData) throws Exception;
	public MailResponse getMail(MailData mailData) throws Exception;
	public BaseResponse deleteMail(MailDelete mailData) throws Exception;
	public String getMailAttachment(MailData mailData) throws Exception;
	public ArrayList<AllMailResponse> searchMail(MailData mailData) throws Exception;
	public MailResponse getReplyMail(MailData mailData) throws Exception;
	public MailResponse replyMail(MailData mailData) throws Exception;
	public MailResponse getReplyAllMail(MailData mailData) throws Exception;
	public MailResponse replyAllMail(MailData mailData) throws Exception;
	public MailResponse getForwardMail(MailData mailData) throws Exception;
	public MailResponse forwardMail(MailData mailData) throws Exception;
	public MailResponse markReadUnread(MailData mailData) throws Exception;
	public ArrayList<AllMailResponse> getArchiveFolderMails(FolderData mailData) throws Exception;
	public MailResponse updateMail(UpdateMail mailData) throws Exception;
	public InviteResponse updateInvite(UserParticipant mailData) throws Exception;
	public ArrayList<AllMailResponse> getContextual(MailData mailData) throws Exception;
	public Attachment downloadAttachment(Attachment attachment) throws Exception;
	
}
