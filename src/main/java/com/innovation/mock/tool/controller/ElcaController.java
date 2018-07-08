package com.innovation.mock.tool.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.innovation.mock.tool.entity.ElcaData;
import com.innovation.mock.tool.entity.ServerProfile;
import com.innovation.mock.tool.entity.ServerProfileCollection;
import com.innovation.mock.tool.util.Constants;

@Controller
@RequestMapping("/elca")
public class ElcaController {

	@Autowired
	private ServerProfileCollection serverProfiles;
	
	@RequestMapping(method = RequestMethod.GET, produces = "text/html")
	public String index(Model model) {
		model.addAttribute(Constants.ELCA_DATA, new ElcaData());
		System.out.println("result: ");
		return "elca";
	}

	@RequestMapping(value = "/sendElcaRequest", method = RequestMethod.POST)
	public String updateRequest(@ModelAttribute(Constants.ELCA_DATA) ElcaData elcaData, Model model) {
		model.addAttribute(Constants.ELCA_DATA, elcaData);
		buildBankNumber(elcaData);
		String result = callWebservice(elcaData);
		model.addAttribute("result", result);
		return "elca";
	}

	private String callWebservice(ElcaData data) {
		String responseString = "";
		String outputString = "";
		try {
			String wsURL = getSOAPUrl(data);
			URL url = new URL(wsURL);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			String xmlInput = buildInputTemplate(data);
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", wsURL);
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			OutputStream out = httpConn.getOutputStream();
			out.write(b);
			out.close();
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			
		} catch (Exception e) {
			System.err.println("=====" + e);
		}
		return outputString;
	}
	
	private Optional<ServerProfile> getServerProfile(ElcaData data) {
		String serverKey =data.getProject()+"-"+data.getServer();
		return serverProfiles.getServerProfiles().stream().filter(item->serverKey.equalsIgnoreCase(item.getName())).findFirst();		
	}
	
	private String getSOAPUrl(ElcaData data) {
		String host = "http://";
		Optional<ServerProfile> serverProfile = getServerProfile(data);
		if (serverProfile.isPresent()) {
			host+= serverProfile.get().getHost() + ":" + serverProfile.get().getPort()+serverProfile.get().getElcaWsUrl();
		}
		return host;
	}
	
	private String buildInputTemplate(ElcaData data) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "                    <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:axon=\"http://axon.feedback.service.esprit.extension.qdb/\">\r\n"
				+ "                       <soapenv:Header/>\r\n" + "                       <soapenv:Body>\r\n"
				+ "                          <axon:eDossierFeedback>\r\n"
				+ "                             <edossierIdentifier>" + data.getElcaId() + "</edossierIdentifier>\r\n"
				+ "                             <externalID>" + data.getDocumentId() + "</externalID>\r\n"
				+ "                             <edossierStatus>OK</edossierStatus>\r\n"
				+ "                             <axonIdentifier>AX01012231</axonIdentifier>\r\n"
				+ "                             <FIN_userbk_nr>"+data.getBankNumber()+"</FIN_userbk_nr>\r\n"
				+ "                             <FIN_Kunden_nr>"+data.getDossierId()+"</FIN_Kunden_nr>\r\n"
				+ "                             <FIN_Belegart>"+data.getDocumentType()+"</FIN_Belegart>\r\n"
				+ "                             <FIN_Obj_art/>\r\n"
				+ "                             <status>pending</status>\r\n"
				+ "                          </axon:eDossierFeedback>\r\n"
				+ "                       </soapenv:Body>\r\n" + "                    </soapenv:Envelope>";
	}
	
	private void buildBankNumber(ElcaData elcaData) {
		if("acrevis".equalsIgnoreCase(elcaData.getProject())){
			elcaData.setBankNumber("9600");//Acrevis
		}else {
			elcaData.setBankNumber("9602");//Acrevis
		}
	}
}
