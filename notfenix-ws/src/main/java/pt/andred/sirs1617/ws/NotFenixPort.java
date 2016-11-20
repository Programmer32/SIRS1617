package pt.andred.sirs1617.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


public class NotFenixPort implements NotFenixPortType {


    /**
     * 
     * @param in
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "out", targetNamespace = "")
    @RequestWrapper(localName = "ping", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.Ping")
    @ResponseWrapper(localName = "pingResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.PingResponse")
    @Action(input = "http://ws.sirs1617.andred.pt/NotFenixPort/pingRequest", output = "http://ws.sirs1617.andred.pt/NotFenixPort/pingResponse")
    public String ping( String in){
    	return NotFenixManager.getInstance().ping(in);
    }

    /**
     * 
     * @param in
     * @return
     *     returns boolean
     */
    @WebMethod
    @WebResult(name = "success", targetNamespace = "")
    @RequestWrapper(localName = "login", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.Login")
    @ResponseWrapper(localName = "loginResponse", targetNamespace = "http://ws.sirs1617.andred.pt/", className = "pt.andred.sirs1617.ws.LoginResponse")
    @Action(input = "http://ws.sirs1617.andred.pt/NotFenixPort/loginRequest", output = "http://ws.sirs1617.andred.pt/NotFenixPort/loginResponse")
    public boolean login( String in){
    	return NotFenixManager.getInstance().login(in);
    }

}
