package com.viettel.kcs.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.viettel.kcs.bo.KcsAcceptanceReport;
import com.viettel.kcs.bo.KcsCheckReport;
import com.viettel.kcs.bo.KcsCheckReq;
import com.viettel.kcs.bo.KcsCheckReqEntity;
import com.viettel.kcs.bo.MerEntity;
import com.viettel.kcs.bo.UtilAttachedDocuments;
import com.viettel.kcs.common.AllInOne;
import com.viettel.kcs.common.EscapedRestrictions;
import com.viettel.kcs.dto.KcsListAssetReqDTO;
import com.viettel.kcs.dto.custom.KcsAcceptanceReportAddDTO;
import com.viettel.kcs.dto.custom.KcsAcceptanceReportDeleteDTO;
import com.viettel.kcs.dto.custom.KcsAcceptanceReportSearchDTO;
import com.viettel.kcs.dto.custom.KcsAcceptanceReportUpdateDTO;
import com.viettel.kcs.utils.Constant;

/**
 * @author HATQ
 *
 */
@Repository("acceptanceReportDAO")
@Transactional
public class KcsAcceptanceReportDAOImpl implements KcsAcceptanceReportDao1 {

	private static final Log logger = LogFactory.getLog(KcsAcceptanceReportDAOImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * @param sf
	 */
	public void setSessionFactory(SessionFactory sf) {
		this.sessionFactory = sf;
	}

	/**
	 * Function get all KcsAcceptanceReport
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<KcsAcceptanceReport> getAllKcsAcceptanceReport() {
		Session session = this.sessionFactory.getCurrentSession();
		List<KcsAcceptanceReport> kcsAcceptanceReport = session.createQuery("from KcsAcceptanceReport").list();
		return kcsAcceptanceReport;
	}

	/**
	 * Function get Object KcsAcceptanceReport By Id Kcs AcceptanceReport
	 */
	@Override
	public KcsAcceptanceReport getKcsAcceptanceReportByID(long id) {
		Session session = this.sessionFactory.getCurrentSession();
		KcsAcceptanceReport acceptanceReport = (KcsAcceptanceReport) session.get(KcsAcceptanceReport.class,
				new Long(id));
		return acceptanceReport;
	}

	/**
	 * Function add new KcsAcceptanceReport
	 */
	@Override
	public long addKcsAcceptanceReport(KcsAcceptanceReportAddDTO addDTO) {
		long id = 0l;
		Session session = null;
		Transaction tx = null;
		try {
			session = this.sessionFactory.openSession();
			tx = session.beginTransaction();
			session.flush();

			// TODO check ma bbnt da ton tai chua?
			id = checkCodeKcsAcceptanceReport(session, addDTO.getCodeAR());
			if (id == -1l) {
				return id;
			}

			id = saveKcsAcceptanceReport(addDTO, session);
			tx.commit();
		} catch (HibernateException e) {
			try {
				tx.rollback();
			} catch (RuntimeException rbe) {
				logger.error("Couldn’t roll back transaction", rbe);
			}
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return id;
	}

	@SuppressWarnings("unchecked")
	public Long checkCodeKcsAcceptanceReport(Session session, String codeBBNT) {
		Long rs = 0l;
		Criteria criteria = session.createCriteria(KcsAcceptanceReport.class);
		criteria.add(Restrictions.eq("code", codeBBNT)).setProjection(Projections.property("acceptanceReportId"));
		List<Long> list = new ArrayList<>();
		list = criteria.list();
		if (list != null && list.size() != 0) {
			rs = -1l;
		}
		return rs;
	}

	public long saveKcsAcceptanceReport(KcsAcceptanceReportAddDTO addDTO, Session session) {
		KcsAcceptanceReport acceptanceReport = new KcsAcceptanceReport();
		long id = 0L;
		try {
			acceptanceReport.setCode(addDTO.getCodeAR());
			acceptanceReport.setVerifierGroupId(addDTO.getVerifierGroupIdAR());
			acceptanceReport.setVerifierGroupCode(addDTO.getVerifierGroupCodeAR());
			acceptanceReport.setVerifierGroupName(addDTO.getVerifierGroupNameAR());
			acceptanceReport.setVerifierUserName(addDTO.getVerifierUserNameAR());
			acceptanceReport.setVerifierUserTitle(addDTO.getVerifierUserTitleAR());
			acceptanceReport.setVendorCompany(addDTO.getVendorCompany());
			acceptanceReport.setVendorName(addDTO.getVendorName());
			acceptanceReport.setVendorTitle(addDTO.getVendorTitle());
			acceptanceReport.setCreatorId(addDTO.getCreatorIdAR());
			acceptanceReport.setCreatorName(addDTO.getCreatorName());
			acceptanceReport.setCreatorGroupId(addDTO.getCreatorGroupIdAR());
			acceptanceReport.setUnitCreated(addDTO.getUnitCreated());
			acceptanceReport.setCreatedDate(AllInOne.getddMMMyy(new Date(), "yyyy-MM-dd"));
			acceptanceReport.setType(addDTO.getTypeAR());
			acceptanceReport.setStatus(1); // Tạo mới
			acceptanceReport.setDescription(addDTO.getDescriptionAR());
			acceptanceReport.setBuyerDelegator(addDTO.getBuyerDelegator());
			acceptanceReport.setBuyerTitle(addDTO.getBuyerTitle());
			acceptanceReport.setCheckedExpectContractDate(addDTO.getCheckedExpectContractDate());
			acceptanceReport.setActualAcceptDateContract(addDTO.getActualAcceptDateContract());
			acceptanceReport.setReasonAcceptanceSlow(addDTO.getReasonAcceptanceSlow());
			acceptanceReport.setParCode(addDTO.getParCode());
			acceptanceReport.setContractId(addDTO.getContractId());
			acceptanceReport.setContractCode(addDTO.getContractCode());
			acceptanceReport.setCanceler(addDTO.getCanceler());
			id = (long) session.save(acceptanceReport);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * Function update KcsAcceptanceReport (Biên bản nghiệm thu)
	 */
	@Override
	public void updateKcsAcceptanceReport(KcsAcceptanceReportUpdateDTO updateDTO) {
		try {
			Session session = this.sessionFactory.getCurrentSession();
			// session.beginTransaction();
			KcsAcceptanceReport kcsAcc = getKcsAcceptanceReportByID(updateDTO.getAcceptanceReportId());
			if (!StringUtils.isEmpty(updateDTO.getCode())) {
				kcsAcc.setCode(updateDTO.getCode());
			} else {
				kcsAcc.setCode(updateDTO.getCodeAR());
			}
			kcsAcc.setActualAcceptDateContract(updateDTO.getActualAcceptDateContract());
			kcsAcc.setUnitCreated(updateDTO.getUnitCreated());
			kcsAcc.setCheckedExpectContractDate(updateDTO.getCheckedExpectContractDate());
			kcsAcc.setVerifierGroupId(updateDTO.getVerifierGroupIdAR());
			kcsAcc.setVerifierGroupName(updateDTO.getVerifierGroupNameAR());
			kcsAcc.setVerifierUserName(updateDTO.getVerifierUserName());
			kcsAcc.setVerifierUserTitle(updateDTO.getVerifierUserTitle());
			kcsAcc.setBuyerTitle(updateDTO.getBuyerTitle());
			kcsAcc.setBuyerDelegator(updateDTO.getBuyerDelegator());
			kcsAcc.setVendorName(updateDTO.getVendorName());
			kcsAcc.setVendorTitle(updateDTO.getVendorTitle());
			kcsAcc.setDescription(updateDTO.getDescriptionAR());
			kcsAcc.setReasonAcceptanceSlow(updateDTO.getReasonAcceptanceSlow());

			if (!StringUtils.isEmpty(updateDTO.getParCode())) {
				if (!updateDTO.getParCode().equals(5)) {
					kcsAcc.setReasonAcceptanceSlow("");
				}
				kcsAcc.setParCode(updateDTO.getParCode());
			}

			session.update(kcsAcc);
			// session.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function hủy KcsAcceptanceReport (update trạng thái)
	 */
	@Override
	public void deleteKcsAcceptanceReport(KcsAcceptanceReportDeleteDTO deleteDTO) {
		try {
			Session session = this.sessionFactory.getCurrentSession();
			KcsAcceptanceReport kcsAcc = getKcsAcceptanceReportByID(deleteDTO.getAcceptanceReportId());
			kcsAcc.setCancelReason(deleteDTO.getCancelReason());
			kcsAcc.setCancelDate(AllInOne.getddMMMyy(new Date(), "yyyy-MM-dd"));
			kcsAcc.setCanceler("USER TEST");
			kcsAcc.setStatus(4); // Đã hủy
			session.update(kcsAcc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function tìm kiếm BBNT
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<KcsAcceptanceReport> searchKcsAcceptanceReport(KcsAcceptanceReportSearchDTO searchCondition) {
		Session session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(KcsAcceptanceReport.class);
		List<KcsAcceptanceReport> listKcsAcceptanceReport = new ArrayList<>();

		searchCondition.trimValue();
		if (!StringUtils.isEmpty(searchCondition.getCode())) {
			criteria.add(EscapedRestrictions.ilike("code", searchCondition.getCode()));
		}
		if (!StringUtils.isEmpty(searchCondition.getContractCode())) {
			criteria.add(Restrictions.eq("contractCode", searchCondition.getContractCode()));
		}
		if (!StringUtils.isEmpty(searchCondition.getType()) && !searchCondition.getType().equals(0)) {
			criteria.add(Restrictions.eq("type", searchCondition.getType()));
		}
		if (!StringUtils.isEmpty(searchCondition.getUnitCreated())) {
			criteria.add(Restrictions
					.like("unitCreated", Constant.PERCENT + searchCondition.getUnitCreated() + Constant.PERCENT)
					.ignoreCase());
		}
		if (!StringUtils.isEmpty(searchCondition.getCreatorId())) {
			criteria.add(Restrictions.eq("creatorId", searchCondition.getCreatorId()));
		}
		if (!StringUtils.isEmpty(searchCondition.getStatus()) && !searchCondition.getStatus().equals(0)) {
			criteria.add(Restrictions.eq("status", searchCondition.getStatus()));
		}
		if (!StringUtils.isEmpty(searchCondition.getCreatedDateTO())) {
			criteria.add(Restrictions.ge("createdDate", searchCondition.getCreatedDateTO()));
		}
		if (!StringUtils.isEmpty(searchCondition.getCreatedDateFROM())) {
			criteria.add(Restrictions.le("createdDate", searchCondition.getCreatedDateFROM()));
		}

		criteria.addOrder(Order.desc("acceptanceReportId"));
		listKcsAcceptanceReport = criteria.list();
		return listKcsAcceptanceReport;

	}

	/*
	 * Get id of KcsAcceptanceReport (non-Javadoc)
	 * 
	 * @see com.viettel.kcs.dao.KcsAcceptanceReportDao1#getCurrValSequence()
	 */
	@Override
	public long getCurrValSequence() {
		Session session = this.sessionFactory.getCurrentSession();
		Query query = session
				.createSQLQuery(
						"SELECT last_number as num FROM dba_sequences WHERE sequence_name = 'KCS_ACCEPTANCE_REPORT_SEQ'")
				.addScalar("num", StandardBasicTypes.BIG_INTEGER);
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	/**
	 * Function khôi phục 1 KcsAcceptanceReport
	 */
	@Override
	public void undoKcsAcceptanceReport(long id) {
		try {
			Session session = this.sessionFactory.getCurrentSession();
			KcsAcceptanceReport kcsAcc = getKcsAcceptanceReportByID(id);
			kcsAcc.setStatus(1); // Mới tạo
			session.update(kcsAcc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get dữ liệu khi show màn hình chi tiết BBNT
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<KcsAcceptanceReportAddDTO> getAttachDocByAccId(long id) {
		List<KcsAcceptanceReportAddDTO> list = null;
		Session session = this.sessionFactory.getCurrentSession();
		try {
			StringBuilder queryStr = buildKcsAccReport(id);
			queryStr.append(buildConditionAttachDocByAccId(id));
			SQLQuery query = session.createSQLQuery(queryStr.toString());
			addScalaAttachDocByAccId(query);
			if (!StringUtils.isEmpty(id)) {
				query.setParameter("id", id);
			}
			query.setResultTransformer(Transformers.aliasToBean(KcsAcceptanceReportAddDTO.class));
			list = query.list();
		} catch (HibernateException e) {
			e.getMessage();
		}
		session.flush();
		return list;
	}

	/**
	 * Mapping các trường với db
	 * 
	 * @param query
	 */
	private void addScalaAttachDocByAccId(SQLQuery query) {
		query.addScalar("acceptanceReportId", LongType.INSTANCE);
		query.addScalar("codeAR", StringType.INSTANCE);
		query.addScalar("actualAcceptDateContract", DateType.INSTANCE);
		query.addScalar("checkedExpectContractDate", DateType.INSTANCE);
		query.addScalar("creatorGroupIdAR", IntegerType.INSTANCE);
		query.addScalar("parCode", IntegerType.INSTANCE);
		query.addScalar("verifierGroupIdAR", IntegerType.INSTANCE);
		query.addScalar("verifierGroupCodeAR", StringType.INSTANCE);
		query.addScalar("reasonAcceptanceSlow", StringType.INSTANCE);
		query.addScalar("contractId", LongType.INSTANCE);
		query.addScalar("vendorName", StringType.INSTANCE);
		query.addScalar("contractCode", StringType.INSTANCE);
		query.addScalar("typeAR", IntegerType.INSTANCE);
		query.addScalar("status", IntegerType.INSTANCE);
		query.addScalar("verifierGroupNameAR", StringType.INSTANCE);
		query.addScalar("checkedExpectContractDate", DateType.INSTANCE);
		query.addScalar("verifierUserNameAR", StringType.INSTANCE);
		query.addScalar("verifierUserTitleAR", StringType.INSTANCE);
		query.addScalar("buyerTitle", StringType.INSTANCE);
		query.addScalar("buyerDelegator", StringType.INSTANCE);
		query.addScalar("vendorName", StringType.INSTANCE);
		query.addScalar("vendorTitle", StringType.INSTANCE);
		query.addScalar("descriptionAR", StringType.INSTANCE);
		query.addScalar("attachDocAR", StringType.INSTANCE);
		query.addScalar("attachDocIDAR", LongType.INSTANCE);
		query.addScalar("filePathAR", StringType.INSTANCE);
	}

	/**
	 * Điều kiện get dữ liệu chi tiết BBNT
	 * 
	 * @param id
	 * @return
	 */
	private String buildConditionAttachDocByAccId(long id) {
		StringBuilder subQuery = new StringBuilder();

		subQuery.append(" FROM ");
		subQuery.append(" KCS_ACCEPTANCE_REPORT acc ");
		subQuery.append(
				" LEFT JOIN UTIL_ATTACH_DOCUMENT doc on acc.ACCEPTANCE_REPORT_ID = doc.OBJECT_ID and doc.TYPE IN ("
						+ "'" + Constant.UTIL_ATTACH_DOCUMENT.TYPE_BBNT + "' ," + "'"
						+ Constant.UTIL_ATTACH_DOCUMENT.TYPE_BBNT_PDF + "')");
		subQuery.append(" WHERE ");
		subQuery.append(" '1' = '1' ");
		subQuery.append(" AND acc.ACCEPTANCE_REPORT_ID = :id ");
		subQuery.append(" ");
		return subQuery.toString();
	}

	/**
	 * Map các trường select
	 * 
	 * @param id
	 * @return
	 */
	private StringBuilder buildKcsAccReport(long id) {
		StringBuilder subQuery = new StringBuilder();
		subQuery.append(" SELECT ");
		subQuery.append(" acc.ACCEPTANCE_REPORT_ID as acceptanceReportId, ");
		subQuery.append(" acc.CODE as codeAR, ");
		subQuery.append(" acc.ACTUAL_ACCEPT_DATE_CONTRACT as actualAcceptDateContract, ");
		subQuery.append(" acc.CHECKED_EXPECT_CONTRACT_DATE as checkedExpectContractDate, ");
		subQuery.append(" acc.CREATOR_GROUP_ID as creatorGroupIdAR, ");
		subQuery.append(" acc.PAR_CODE as parCode, ");
		subQuery.append(" acc.VERIFIER_GROUP_ID as verifierGroupIdAR, ");
		subQuery.append(" acc.VERIFIER_GROUP_CODE as verifierGroupCodeAR, ");
		subQuery.append(" acc.REASON_ACCEPTANCE_SLOW as reasonAcceptanceSlow, ");
		subQuery.append(" acc.CONTRACT_ID as contractId, ");
		subQuery.append(" acc.CONTRACT_CODE as contractCode, ");
		subQuery.append(" acc.TYPE as typeAR, ");
		subQuery.append(" acc.STATUS as status, ");
		subQuery.append(" acc.VERIFIER_GROUP_NAME as verifierGroupNameAR, ");
		subQuery.append(" acc.CHECKED_EXPECT_CONTRACT_DATE as checkedExpectContractDate, ");
		subQuery.append(" acc.VERIFIER_USER_NAME as verifierUserNameAR, ");
		subQuery.append(" acc.VERIFER_USER_TITLE as verifierUserTitleAR, ");
		subQuery.append(" acc.BUYER_TITLE as buyerTitle, ");
		subQuery.append(" acc.BUYER_DELEGATOR as buyerDelegator, ");
		subQuery.append(" acc.VENDOR_NAME as vendorName, ");
		subQuery.append(" acc.VENDOR_TITLE as vendorTitle, ");
		subQuery.append(" acc.VENDOR_COMPANY as vendorCompany, ");
		subQuery.append(" acc.DESCRIPTION as descriptionAR, ");
		subQuery.append(" doc.UTIL_ATTACH_DOCUMENT_ID as attachDocIDAR, ");
		subQuery.append(" doc.NAME as attachDocAR, ");
		subQuery.append(" doc.FILE_PATH as filePathAR ");
		return subQuery;
	}

	/**
	 * Get MerEntityID by ContractID
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getMerEntityIDByContractID(long id) {
		Session session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(MerEntity.class);
		criteria.add(Restrictions.eq("cntContractId", id)).setProjection(Projections.property("id"));
		List<Long> list = new ArrayList<>();
		list = criteria.list();
		return list;
	}

	/**
	 * Get List CheckReqId by list Mer_entity_id
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getCheckReqIDByMerID(List<Long> listID) {
		Session session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(KcsCheckReqEntity.class);
		criteria.add(Restrictions.in("merEntityId", listID)).setProjection(Projections.property("checkReqId"));
		List<Long> list = new ArrayList<>();
		list = criteria.list();
		return list;
	}

	/**
	 * Get List Status by List CheckReqId
	 */
	@Override
	public List<String> getStatusCheckReqByCheckReqID(List<Long> listID) {
		Session session = this.sessionFactory.getCurrentSession();
		List<String> list = new ArrayList<>();
		for (long id : listID) {
			Criteria criteria = session.createCriteria(KcsCheckReport.class);
			criteria.add(Restrictions.eq("checkReqIdFk", id));
			criteria.setProjection(Projections.property("id"));
			if (criteria.list().size() != 0) {
				list.add(Constant.AcceptanceReport.TRUE);
			} else {
				list.add(Constant.AcceptanceReport.FALSE);
			}
		}
		return list;
	}

	/**
	 * Function get danh sách tài sản theo KcsCheckReqID
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<KcsListAssetReqDTO> getKcsAssetByIdAcc(Long checkReqId) {
		List<KcsListAssetReqDTO> lstKcsListAssetReqDTO = new ArrayList<>();
		Session session = this.sessionFactory.getCurrentSession();
		try {
			StringBuilder queryStr = buildKcsAssetReq();
			queryStr.append(buildConditionKcsAssetById(checkReqId));
			SQLQuery query = session.createSQLQuery(queryStr.toString());
			addScalaKcsAssetReq(query);
			setParamsKcsAssetReq(checkReqId, query);
			query.setResultTransformer(Transformers.aliasToBean(KcsListAssetReqDTO.class));
			lstKcsListAssetReqDTO = query.list();
		} catch (HibernateException e) {
			e.getMessage();
		}
		session.flush();
		return lstKcsListAssetReqDTO;
	}

	private StringBuilder buildKcsAssetReq() {
		StringBuilder subQuery = new StringBuilder();
		subQuery.append(" SELECT ");
		subQuery.append(" a.CHECK_REQ_ID as checkReqId, ");
		subQuery.append(" a.CHECK_REQ_ENTITY_ID as checkReqEntityId, ");
		subQuery.append(" b.PART_NUMBER as partNumber, ");
		subQuery.append(" b.SERIAL as serial, ");
		subQuery.append(" d.IS_SERIAL as chungLoai, ");
		subQuery.append(" d.NAME as tenHang, ");
		subQuery.append(" d.CODE as maHang, ");
		subQuery.append(" a.QUANTITY as soLuong, ");
		subQuery.append(" a.QUANTITY_GOODS_AGREED_ENTER as SLNhap, ");
		subQuery.append(" b.CAT_UNIT_NAME as dvTinh, ");
		subQuery.append(" sh.CONTRACT_CODE as maHD, ");
		subQuery.append(" sh.CODE as maLoHang, ");
		subQuery.append(" d.STATUS as ttHangHoa, ");
		subQuery.append(" b.MANUFACTURER_NAME as hangSX, ");
		subQuery.append(" b.PRODUCING_COUNTRY_NAME as nuocSX, ");
		subQuery.append(" d.DESCRIPTION as mota ");
		return subQuery;
	}

	private String buildConditionKcsAssetById(Long checkReqId) {
		StringBuilder subQuery = new StringBuilder();
		subQuery.append(" FROM KCS_CHECK_REQ kcr ");
		subQuery.append(" JOIN KCS_CHECK_REQ_ENTITY a on a.CHECK_REQ_ID = kcr.CHECK_REQ_ID ");
		subQuery.append(" JOIN MER_ENTITY b on b.MER_ENTITY_ID = a.MER_ENTITY_ID ");
		subQuery.append(" JOIN GOODS d ON b.GOODS_ID = d.GOODS_ID ");
		subQuery.append(" LEFT JOIN SHIPMENT sh on sh.SHIPMENT_ID = b.SHIPMENT_ID ");
		subQuery.append(" WHERE ");
		subQuery.append(" '1' = '1' ");
		if (!StringUtils.isEmpty(checkReqId)) {
			subQuery.append(" AND UPPER(kcr.CHECK_REQ_ID) LIKE :checkReqId ");
		}
		subQuery.append(" ");
		return subQuery.toString();
	}

	private void addScalaKcsAssetReq(SQLQuery query) {
		query.addScalar("checkReqId", LongType.INSTANCE);
		query.addScalar("checkReqEntityId", LongType.INSTANCE);
		query.addScalar("serial", StringType.INSTANCE);
		query.addScalar("chungLoai", StringType.INSTANCE);
		query.addScalar("partNumber", StringType.INSTANCE);
		query.addScalar("tenHang", StringType.INSTANCE);
		query.addScalar("maHang", StringType.INSTANCE);
		query.addScalar("soLuong", StringType.INSTANCE);
		query.addScalar("SLNhap", StringType.INSTANCE);
		query.addScalar("dvTinh", StringType.INSTANCE);
		query.addScalar("maHD", StringType.INSTANCE);
		query.addScalar("maLoHang", StringType.INSTANCE);
		query.addScalar("ttHangHoa", StringType.INSTANCE);
		query.addScalar("hangSX", StringType.INSTANCE);
		query.addScalar("nuocSX", StringType.INSTANCE);
		query.addScalar("mota", StringType.INSTANCE);
	}

	private void setParamsKcsAssetReq(Long checkReqId, SQLQuery query) {
		if (checkReqId == null)
			return;

		if (!StringUtils.isEmpty(checkReqId)) {
			query.setParameter("checkReqId", checkReqId);
		}
	}

	/**
	 * Get List CheckReqId by list ContractId
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getCheckReqIDByContractID(Long contractID) {
		Session session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(KcsCheckReq.class);
		criteria.add(Restrictions.eq("contractId", contractID)).setProjection(Projections.property("checkReqId"));
		List<Long> list = new ArrayList<>();
		list = criteria.list();
		return list;
	}

	/**
	 * Function upload file
	 */
	@Override
	public String uploadFile(UtilAttachedDocuments attachedDocuments) {
		try {
			Session session = this.sessionFactory.getCurrentSession();
			Long idAttDoc = (Long) session.save(attachedDocuments);
			if (idAttDoc > 0L) {
				return Constant.KcsCheckReq.OK;
			} else {
				return Constant.KcsCheckReq.KO;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Constant.KcsCheckReq.KO;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<UtilAttachedDocuments> getListAttachedDoc(long id, String type) {
		Session session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(UtilAttachedDocuments.class);
		
		List<UtilAttachedDocuments> list = new ArrayList<>();
		
		criteria.add(Restrictions.eq("objectId", id));
		criteria.add(Restrictions.eq("type", type));
		
		list = criteria.list();
		
		return list;
	}
	
	@Override
	public void deleteFileSignBBNT(Long acceptanceId) {
		List<UtilAttachedDocuments> attDoc = getListAttachedDoc(acceptanceId, Constant.UTIL_ATTACH_DOCUMENT.TYPE_BBNT_PDF);
		if (attDoc != null && attDoc.size() != 0 && attDoc.get(0).getUtilAttachDocumentId() != null) {
			Session session = null;
			Transaction tx = null;
			try {
				session = this.sessionFactory.openSession();
				tx = session.beginTransaction();
				session.flush();
				
				String sql = "DELETE FROM UTIL_ATTACH_DOCUMENT WHERE UTIL_ATTACH_DOCUMENT_ID = :idUtil ";
				Query query = session.createSQLQuery(sql);
				query.setParameter("idUtil", attDoc.get(0).getUtilAttachDocumentId());
				query.executeUpdate();

				tx.commit();
			} catch(HibernateException e){
				try{
					tx.rollback();
				}catch(RuntimeException rbe){
					logger.error("Couldn’t roll back transaction", rbe);
				}
				throw e;
			} catch (Exception e) {
				e.getMessage();
			} finally {
				if (session != null) {
					session.close();
				}
			}
		}
	}
}
