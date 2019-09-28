package com.liftago.demo;

import java.util.Hashtable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository
@Scope("singleton")
public class EmailsRepository {

	/**
	 * Used data structure is synchronized and has O(1) lookup and store complexity.
	 */
	Hashtable<String, String> email_name = new Hashtable<String, String>();

	public boolean hasEmail(String email) {
		return email_name.containsKey(email);
	}

	public void storeEmail(String email, String name) {
		email_name.put(email, name);
	}
}
