/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.nilsjarh.ntnu.mobapp4.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import lombok.extern.java.Log;
import no.nilsjarh.ntnu.mobapp4.domain.Item;
import no.nilsjarh.ntnu.mobapp4.domain.Purchase;
import no.nilsjarh.ntnu.mobapp4.domain.User;
import no.nilsjarh.ntnu.mobapp4.beans.*;
import no.nilsjarh.ntnu.mobapp4.resources.DatasourceProducer;

/**
 *
 * @author nils
 */
@Log
@Stateless
public class PurchaseBean {

	/**
	 * The application server will inject a DataSource as a way to
	 * communicate with the database.
	 */
	@Resource(lookup = DatasourceProducer.JNDI_NAME)
	DataSource dataSource;

	/**
	 * The application server will inject a EntityManager as a way to
	 * communicate with the database via JPA.
	 */
	@PersistenceContext
	EntityManager em;

	@Inject
	ItemBean ib;

	@Inject
	MailBean mb;

	public Purchase addPurchase(User buyer, Long itemId) {
		System.out.println("=== PURCHASE EJB: CREATE PURCHASE ===");
		System.out.print("Query parameters: buyer:" + buyer);
		Item item = ib.getItem(itemId);

		if (item == null || buyer == null) {
			System.out.println("- Status...........: " + "Item or buyer not found");
			System.out.println("- Item UID.........: " + itemId);
			System.out.println("- Buyer............: " + buyer);
			return null;
		} else if (item.getPurchase() != null) {
			System.out.println("- Status...........: " + "Item already sold");
			System.out.println("- Item UID.........: " + itemId);
			System.out.println("- Purchase id......: " + item.getPurchase().getId());
			return null;
		}

		try {
			Purchase p = new Purchase(buyer);
			Purchase o = em.merge(p);
			em.flush();

			ib.prepareItemForEdit(item);
			item.setPurchase(o);
			ib.saveItemFromEdit(item);
			em.refresh(o);
			em.refresh(item);
			
			User seller = item.getSellerUser();

			System.out.println("=== PURCHASE EJB: CREATE PURCHASE ===");
			System.out.println("- Status...........: " + "Purchase completed");
			System.out.println("- In database as id: " + o.getId());
			System.out.println("- Buyer............: " + buyer);
			System.out.println("- Seller...........: " + seller);
			System.out.println("- Price ........NOK: " + item.getPriceNok());
			System.out.println("- Completed on.....: " + o.getPurchaseDate());
			System.out.println("- Item UID.........: " + item.getId());
			return o;

		} catch (Exception e) {

			System.out.println("- Status...........: " + "Purchase FAILED");
			System.out.println("- Seller...........: " + item.getSellerUser());
			System.out.println("- Price ........NOK: " + item.getPriceNok());
			System.out.println("- Item UID.........: " + item.getId());
			System.err.println(e);
			return null;
		}
	}
	
	public Purchase getPurchase(Long id) {
		System.out.println("=== PURCHASE EJB: GET PURCHASE ===");
		System.out.print("Query parameters: id:" + id);
		if (id == null) {
			return null;
		}
		Purchase found = em.find(Purchase.class, id);
		if (found == null) {
			return null;
		}
		em.refresh(found);
		System.out.println("- Status.........: " + "In database");
		System.out.println("- Id.............: " + found.getId());
		return found;
	}

	public boolean verifyOwnedPurchase(Purchase p, User purchaser) {
		if (p != null) {
			if (p.getBuyerUser().equals(purchaser)) {
				return true;
			}
		}
		return false;
	}

	public boolean deletePurchase(Purchase purchase) {
		System.out.println("=== PURCHASE EJB: DELETE PURCHASE ===");
		if (purchase != null) {
			Long id = purchase.getId();
			if (id == null) {
				return false;
			}
			System.out.println("- Found purchase id..: " + id);
			em.remove(purchase);
			em.flush();
			if (getPurchase(id) == null) {
				System.out.println("- Status.........: " + "DELETED");
				return true;
			}
		}
		System.out.print("State..........:" + "ERROR ON DELETE");
		return false;
	}

	public List<Purchase> getPurchaseListByBuyerQuery(User buyer) {
		if (buyer == null) {
			return new ArrayList<>();
		}
		System.out.println("=== PURCHASE EJB: FIND PURCHASES BY USER QUERY ===");
		Query query = em.createNamedQuery(Purchase.FIND_PURCHASES_BY_USER);
		query.setParameter("buyer", buyer.getId());

		List<Purchase> foundPurchases = new ArrayList<>(query.getResultList());

		System.out.println("- Buyer.............: " + buyer.getId());
		System.out.println("- Done purchases.....: " + returnPurchaseNames(foundPurchases));
		return foundPurchases;
	}

	public List<Purchase> getAllPurchases() {
		System.out.println("=== PURCHASE EJB: FIND ALL PURCHASES QUERY ===");
		Query query = em.createNamedQuery(Purchase.FIND_ALL_PURCHASES);

		List<Purchase> purchases = new ArrayList<>(query.getResultList());
		System.out.println("- Found items........: " + returnPurchaseNames(purchases));

		return purchases;
	}

	private String returnPurchaseNames(List<Purchase> list) {
		if (list.isEmpty()) {
			return "<none>";
		}
		StringBuilder sb = new StringBuilder();
		for (Purchase element : list) {
			sb.append("\n[");
			sb.append(element.getPurchaseDate()).append("-");
			sb.append(element.getId()).append(":");
			sb.append(element.getItem().getId()).append(" kr ");
			sb.append(element.getItem().getTitle()).append(" kr ");
			sb.append(element.getItem().getPriceNok());
			sb.append("], ");
		}
		return sb.toString();
	}
}
