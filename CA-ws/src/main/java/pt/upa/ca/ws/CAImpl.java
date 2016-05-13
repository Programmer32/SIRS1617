package pt.upa.ca.ws;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Map;
import java.util.TreeMap;

import javax.jws.WebService;

import sun.misc.BASE64Encoder;

@WebService(endpointInterface = "pt.upa.ca.ws.CA")
public class CAImpl implements CA {

	private static String KEY_PATH            = "./src/main/resources/";
//	private static String PUB_ENDING          = "pub.key";
//	private static String PRIV_ENDING         = "priv.key";
	private static String CERT_ENDING 		  = ".cer";
	private static String CA_CERTIFICATE_FILE = "../CA-ws/src/main/resources/ca-certificate.pem.txt";

	public CAImpl() {

		File folder = new File(KEY_PATH);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String name = listOfFiles[i].getName();
//				System.out.println("FileNAme: " + name);
				if (name.endsWith(CERT_ENDING)){
					byte[] cerEncoded = null;
					try {
//						System.out.println("Reading: " + KEY_PATH + name);
						cerEncoded = readCertificateFile(KEY_PATH + name);
					} catch (CertificateException | IOException e) {
						//Everything Will Be Okay..
						System.out.println("\u001B[31;1mReading " + name + " went Wrong... : " + e.getClass() + " : " + e.getMessage()+ "\u001B[0m");
					}
					String cer = bytes2String(cerEncoded);
					int index = name.lastIndexOf(CERT_ENDING);
					String ws_name = name.substring(0,index);
					pubKeys.put(ws_name, cer);
					System.out.println("\u001B[33;1mRead entitity! : " + ws_name + "\u001B[0m");
				}
			} else {	/* IGNORE NOT FILES */ }
		}
	}

	private Map<String, String> pubKeys = new TreeMap<String, String>();
	/*
	 * public String getKeyByName(String name){ for (java.util.Map.Entry<String,
	 * String> entry : pubKeys.entrySet()) { if (Objects.equals(name,
	 * entry.getValue())) { return entry.getKey(); } } return null; }
	 */

	@Override
	public void addEntity(String name) {
		System.out.println("addEntity received: " + name);
/*
		String pubKey = pubKeys.get(name);
		if (pubKey != null) {
			System.out.println("\u001B[35;1mEntity already existed, " +name+ " not created\u001B[0m");
			return;
		}

		String pub = null;
		try {
			pub = write(KEY_PATH + name + PUB_ENDING, KEY_PATH + name + PRIV_ENDING);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pubKeys.put(name, pub);
		System.out.println("\u001B[33;1mCreated: " + name + "\u001B[0m");
*/
		System.out.println("\u001B[33;1mNothing done, not even checked for file existence\u001B[0m");
	}

	@Override
	public String getPublicKey(String name) throws EntityNotFoundException {
		System.out.println("\u001B[34mgetPublicKey request received: \u001B[0m" + name);
		String pubKey = pubKeys.get(name);
		if (pubKey != null) {
			return pubKeys.get(name);
		}
		throw new EntityNotFoundException("Entity: " + name);

	}
/*
	public static String write(String publicKeyPath, String privateKeyPath) throws Exception {

		// generate RSA key pair

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair key = keyGen.generateKeyPair();

		// System.out.println("Writing public key to " + publicKeyPath + "
		// ...");
		byte[] pubEncoded = key.getPublic().getEncoded();
		writeFile(publicKeyPath, pubEncoded);

		System.out.println("Writing private key to '" + privateKeyPath + "' ...");
		byte[] privEncoded = key.getPrivate().getEncoded();
		writeFile(privateKeyPath, privEncoded);
		System.out.println("Done with the keys");
		return bytes2String(pubEncoded);
	}

	private static void writeFile(String path, byte[] content) throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content);
		fos.close();
	}
*/
	private static String bytes2String(byte[] message) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(message);
	}

	private static byte[] readFile(String path) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(path);
		byte[] content = new byte[fis.available()];
		fis.read(content);
		fis.close();
		return content;
	}

    /**
     * Reads a certificate from a file
     *
     * @return
     * @throws IOException
     */
    private byte[] readCertificateFile(String certificateFilePath) throws IOException, CertificateException {

        Certificate certificate = readCertificate(certificateFilePath);

        Certificate caCertificate = readCertificate(CA_CERTIFICATE_FILE);
        PublicKey caPublicKey = caCertificate.getPublicKey();

        if (verifySignedCertificate(certificate, caPublicKey)) {
            System.out.println("The asked certificate is valid");
        } else {
            System.err.println("The asked certificate is not valid");
            throw new CertificateException();
        }
        return Files.readAllBytes(Paths.get(certificateFilePath));

    }
    
    /**
     * Reads a certificate from a file
     *
     * @return
     * @throws Exception
     */
    private Certificate readCertificate(String certificateFilePath) throws IOException, CertificateException {
        FileInputStream fis;

        try {
            fis = new FileInputStream(certificateFilePath);
        } catch (FileNotFoundException e) {
            System.err.println("Certificate file <" + certificateFilePath + "> not found.");
            return null;
        }
        BufferedInputStream bis = new BufferedInputStream(fis);

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        if (bis.available() > 0) {
            Certificate cert = cf.generateCertificate(bis);
            return cert;
            // It is possible to print the content of the certificate file:
            // System.out.println(cert.toString());
        }
        bis.close();
        fis.close();
        return null;
    }
    
    /**
     * Verifica se um certificado foi devidamente assinado pela CA
     *
     * @param certificate
     *            certificado a ser verificado
     * @param caPublicKey
     *            certificado da CA
     * @return true se foi devidamente assinado
     */
    private boolean verifySignedCertificate(Certificate certificate, PublicKey caPublicKey) {
        try {
            certificate.verify(caPublicKey);
        } catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
                | SignatureException e) {
            // O método Certifecate.verify() não retorna qualquer valor (void).
            // Quando um certificado é inválido, isto é, não foi devidamente
            // assinado pela CA
            // é lançada uma excepção: java.security.SignatureException:
            // Signature does not match.
            // também são lançadas excepções caso o certificado esteja num
            // formato incorrecto ou tenha uma
            // chave inválida.

            return false;
        }
        return true;
    }
	
}
