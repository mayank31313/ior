package ai.mayank.iot.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.mayank.iot.tables.EndpointHits;

@Service
public class EndpointHitsService {
	@Autowired
	private SessionFactory sessionFactory;
	Logger log = Logger.getLogger(EndpointHitsService.class.getName());
	@Transactional
	public void update(EndpointHits hits) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(hits);
	}
	@Transactional
	public List<EndpointHits> getAll(String token) {
		Session session = sessionFactory.getCurrentSession();
		Query<EndpointHits> query = session.createQuery("from EndpointHits p where p.user=:user ORDER BY p.id",EndpointHits.class);
		query.setString("user", token);
		query.setMaxResults(10);
		return query.list();
	}
	
	@Transactional
	public EndpointHits get(String token) {
		Session session = sessionFactory.getCurrentSession();
		Query<EndpointHits> query = session.createQuery("from EndpointHits where user=:user and date=:date",EndpointHits.class);
		query.setString("user", token);
		query.setParameter("date", new Date());
		List<EndpointHits> list = query.list();
		if(list.isEmpty())
			return null;
		return list.get(0);
	}
	
	
	@Transactional
	public void incrementHit(String id) {
		Session session = sessionFactory.getCurrentSession();
		Query<EndpointHits> query = session.createQuery("update EndpointHits set hits=hits+1 where user=:user and date=:date",EndpointHits.class);
		query.setString("user", id);
		query.setParameter("date",new Date(),DateType.INSTANCE);
		Integer updated = query.executeUpdate();
		log.info("Updated: " + updated.toString());
	}
}
