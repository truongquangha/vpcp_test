package com.viettel.kcs.dao;

import java.util.List;

import com.viettel.kcs.bo.KcsAcceptanceReport;
import com.viettel.kcs.bo.UtilAttachedDocuments;
import com.viettel.kcs.dto.KcsListAssetReqDTO;
import com.viettel.kcs.dto.custom.KcsAcceptanceReportAddDTO;
import com.viettel.kcs.dto.custom.KcsAcceptanceReportDeleteDTO;
import com.viettel.kcs.dto.custom.KcsAcceptanceReportSearchDTO;
import com.viettel.kcs.dto.custom.KcsAcceptanceReportUpdateDTO;

/**
 * @author HATQ
 *
 */
public interface KcsAcceptanceReportDao1 {

	public List<KcsAcceptanceReport> getAllKcsAcceptanceReport();
	
	public List<KcsAcceptanceReport> searchKcsAcceptanceReport(KcsAcceptanceReportSearchDTO searchCondition);

	public KcsAcceptanceReport getKcsAcceptanceReportByID(long id);

	public long addKcsAcceptanceReport(KcsAcceptanceReportAddDTO addDTO);
	
	public long getCurrValSequence();

	public void updateKcsAcceptanceReport(KcsAcceptanceReportUpdateDTO updateDTO);

	public void deleteKcsAcceptanceReport(KcsAcceptanceReportDeleteDTO deleteDTO);
	
	public void undoKcsAcceptanceReport(long id);
	
	public List<KcsAcceptanceReportAddDTO> getAttachDocByAccId(long id);
	
	public List<Long> getMerEntityIDByContractID(long id);
	
	public List<Long> getCheckReqIDByMerID(List<Long> listID);
	
	public List<Long> getCheckReqIDByContractID(Long contractId);
	
	public List<String> getStatusCheckReqByCheckReqID(List<Long> listID);
	
	public List<KcsListAssetReqDTO> getKcsAssetByIdAcc(Long checkReqId);
	
	public String uploadFile(UtilAttachedDocuments attachedDocuments);
	
	public List<UtilAttachedDocuments> getListAttachedDoc(long id, String type);
	
	public void deleteFileSignBBNT(Long acceptanceId);
}
