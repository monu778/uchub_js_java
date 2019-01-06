package com.broadsoft.demohub.api.manager;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.broadsoft.demohub.api.adapter.MailAdapter;
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
public class MailManager implements MailManagerIF {

	@Autowired
	MailAdapter mailAdapter;
	
	@Override
	public MailData getCount(MailData mailData) throws Exception {
		return mailAdapter.getCount(mailData);
	}
	
	@Override
	public ArrayList<AllMailResponse> getAllMails(MailData mailData) throws Exception {
		return mailAdapter.getAllMails(mailData);
	}
	
	@Override
	public BaseResponse deleteMail(MailDelete mailData) throws Exception {
		return mailAdapter.deleteMail(mailData);
	}
	
	@Override
	 public String getMailAttachment(MailData mailData) throws Exception {
	  return mailAdapter.getMailAttachment(mailData);
	 }

	@Override
	public ArrayList<AllMailResponse> searchMail(MailData mailData) throws Exception {
		return mailAdapter.searchMail(mailData);
	}

	@Override
	public MailResponse getReplyMail(MailData mailData) throws Exception {
		return mailAdapter.getReplyMail(mailData);
	}

	@Override
	public MailResponse getReplyAllMail(MailData mailData) throws Exception {
		return mailAdapter.getReplyAllMail(mailData);
	}
	
	@Override
	public MailResponse replyAllMail(MailData mailData) throws Exception {
		return mailAdapter.replyAllMail(mailData);
	}

	@Override
	public MailResponse getForwardMail(MailData mailData) throws Exception {
		return mailAdapter.getForwardMail(mailData);
	}
	

	@Override
	public MailResponse markReadUnread(MailData mailData) throws Exception {
		return mailAdapter.markReadUnread(mailData);
	}
	
	@Override
	public ArrayList<AllMailResponse> getArchiveFolderMails(FolderData mailData) throws Exception{
		return mailAdapter.getArchiveMails(mailData);
	}

	@Override
	public MailResponse updateMail(UpdateMail mailData) throws Exception{
		return mailAdapter.updateMail(mailData);
	}

	@Override
	public InviteResponse updateInvite(UserParticipant mailData) throws Exception {
		return mailAdapter.updateInvite(mailData);
	}

	@Override
	public MailResponse getMail(MailData mailData) throws Exception {
		return mailAdapter.getMail(mailData);
		
	}

	@Override
	public MailResponse forwardMail(MailData mailData) throws Exception {
		return mailAdapter.forwardMail(mailData);
	}

	@Override
	public MailResponse replyMail(MailData mailData) throws Exception {
		return mailAdapter.replyMail(mailData);
	}

	@Override
	public ArrayList<AllMailResponse> getContextual(MailData mailData) throws Exception {
		return mailAdapter.getContextual(mailData);
	}

	@Override
	public Attachment downloadAttachment(Attachment attachmentRequest) throws Exception {
		return mailAdapter.downloadAttachment(attachmentRequest);
	}

}
