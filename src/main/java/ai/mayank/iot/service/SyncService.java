package ai.mayank.iot.service;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.mayank.iot.tables.DeviceSync;
import ai.mayank.iot.tables.Widgets;

@Service
public class SyncService {
	@Autowired
	private SessionFactory sessionFactory;
	
	@Transactional
	public void addSync(DeviceSync sync) {
		Session session = sessionFactory.getCurrentSession();
		session.save(sync);
	}
	
	@Transactional
	public void addWidget(Widgets widget) {
		Session session = sessionFactory.getCurrentSession();
		session.save(widget);
	}
	
	@Transactional(readOnly = true)
	public List<Widgets> getWidgets(String token){
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Widgets.class);
		HashMap<String,String> map = new HashMap<>();
		map.put("user", token);
		criteria.add(Restrictions.allEq(map));
		return criteria.list();
	}
	@Transactional(readOnly = true)
	public DeviceSync getSync(String token,Integer code,String port) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(DeviceSync.class);
		HashMap<String,Object> map = new HashMap<>();
		map.put("user",token);
		map.put("code",code);
		map.put("port",port);
		criteria.add(Restrictions.allEq(map));
		criteria.addOrder(Order.desc("id"));
		criteria.setMaxResults(1);
		return (DeviceSync)criteria.uniqueResult();
	}
	
	@Transactional
	public Widgets delete(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Widgets widget = session.get(Widgets.class, id);
		session.delete(widget);
		return widget;
	}
}
