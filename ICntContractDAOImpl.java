/**
 * 
 */
package com.viettel.kcs.dao;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.viettel.kcs.bo.ICntContract;
import com.viettel.kcs.dto.ICntContractDTO;
import com.viettel.kcs.dto.custom.ICntContractDetailDTO;
import com.viettel.kcs.dto.custom.KcsAcceptanceReportAddDTO;
import com.viettel.kcs.dto.custom.KcsAutocompleteSearchDto;
import com.viettel.service.base.dao.BaseFWDAOImpl;

/**
 * @author HATQ
 *
 */
@Repository("iCntContractDAO")
@Transactional
public class ICntContractDAOImpl extends BaseFWDAOImpl<ICntContract,Long> implements ICntContractDAO  {

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
	 * Get ICntContract by ID
	 * 
	 * @param id
	 * @return ICntContract
	 */
	@Override
	public ICntContract getICntContractByID(long id) {
		Session session = this.sessionFactory.getCurrentSession();
		ICntContract cntContract = (ICntContract) session.get(ICntContract.class,
				new Long(id));
		return cntContract;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ICntContractDTO> filterStartWidth(KcsAutocompleteSearchDto searchForm) {
		String queryStr = buildContractGetList(searchForm);
		SQLQuery query = getSession().createSQLQuery(queryStr);
		addScalaInvestPlanObject(query);

		if (!StringUtils.isEmpty(searchForm.getKeySearch())) {
			query.setParameter("keySearch", "%" + searchForm.getKeySearch().trim().toUpperCase() + "%");
		}

		query.setResultTransformer(Transformers.aliasToBean(ICntContractDTO.class));
		query.setMaxResults(searchForm.getPageSize());
		return query.list();
		
	}

	private void addScalaInvestPlanObject(SQLQuery query) {
		query.addScalar("contractId", LongType.INSTANCE);
		query.addScalar("code", StringType.INSTANCE);
		query.addScalar("contractName", StringType.INSTANCE);
		
	}

	private String buildContractGetList(KcsAutocompleteSearchDto searchForm) {
		StringBuilder sbquery = new StringBuilder();
		// SELECT
		sbquery.append(" SELECT ");
		sbquery.append(" 	icc.CNT_CONTRACT_ID AS contractId, ");
		sbquery.append(" 	icc.CODE AS code, ");
		sbquery.append(" 	icc.NAME AS contractName ");
		sbquery.append(" FROM ");
		sbquery.append(" 	I_CNT_CONTRACT icc ");
		sbquery.append(" WHERE ");
		sbquery.append(" 	1 = 1 ");

		if (!StringUtils.isEmpty(searchForm.getKeySearch())) {
			sbquery.append(" 	AND (upper(icc.CODE) like :keySearch ");
			sbquery.append(" 		OR upper(icc.NAME) like :keySearch) ");
		}
		sbquery.append(" 	ORDER BY contractName ");
		return sbquery.toString();
	}


	@SuppressWarnings("unchecked")
	@Override
	public ICntContractDetailDTO getICntContractDetailByID(long id) {
		List<ICntContractDetailDTO> list = null;
		String queryStr = buildContractDetailGetList(id);
		SQLQuery query = getSession().createSQLQuery(queryStr);
		addScalaContractDetail(query);
		if (!StringUtils.isEmpty(id)) {
			query.setParameter("id", id);
		}
		query.setResultTransformer(Transformers.aliasToBean(ICntContractDetailDTO.class));
		list = query.list();
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	private String buildContractDetailGetList(long id) {
		StringBuilder sbquery = new StringBuilder();
		// SELECT
		sbquery.append(" SELECT ");
		sbquery.append(" 	icc.CNT_CONTRACT_ID AS contractId, ");
		sbquery.append(" 	icc.CODE AS contractCode, ");
		sbquery.append(" 	icc.name AS contractName, ");
		sbquery.append(" 	icc.SYS_GROUP_ID AS sysGroupId, ");
		sbquery.append(" 	icc.PRICE AS price, ");
		sbquery.append(" 	sysG.NAME AS sysGroupName ");
		sbquery.append(" FROM ");
		sbquery.append(" 	I_CNT_CONTRACT icc ");
		sbquery.append(
				" LEFT JOIN SYS_GROUP sysG on icc.SYS_GROUP_ID = sysG.SYS_GROUP_ID ");
		sbquery.append(" WHERE ");
		sbquery.append(" 	1 = 1 ");
		sbquery.append(" AND icc.CNT_CONTRACT_ID = :id ");
		
		return sbquery.toString();
	}
	
	private void addScalaContractDetail(SQLQuery query) {
		query.addScalar("contractId", LongType.INSTANCE);
		query.addScalar("contractCode", StringType.INSTANCE);
		query.addScalar("contractName", StringType.INSTANCE);
		query.addScalar("sysGroupId", LongType.INSTANCE);
		query.addScalar("sysGroupName", StringType.INSTANCE);
		query.addScalar("price", IntegerType.INSTANCE);
		
	}


}
