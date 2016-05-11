package pt.upa.ca.ws;

import javax.jws.WebService;

@WebService
public interface CA {
	void addEntity(String name);
	String getPublicKey(String name) throws EntityNotFoundException;	
}
