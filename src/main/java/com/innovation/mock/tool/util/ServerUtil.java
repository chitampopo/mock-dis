package com.innovation.mock.tool.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;

import com.innovation.mock.tool.entity.Server;
import com.innovation.mock.tool.entity.ServerProfile;
import com.innovation.mock.tool.entity.ServerProfileCollection;

public class ServerUtil {

	@Autowired
	private static ServerProfileCollection serverProfiles;
	
	public static Server updateServerInfo(Server server) {
		Optional<ServerProfile> serverProfile = findCurrentServerProfile(server);
		
		if(serverProfile.isPresent()) {
			server.setHost(serverProfile.get().getHost());
			server.setPort(serverProfile.get().getPort());
			server.setApplication(serverProfile.get().getEnvironmentApplication());
			server.setSshUsername(serverProfile.get().getSshUsername());
			server.setSshPassword(serverProfile.get().getSshPassword());
		}
		return server;
	}

	public static Optional<ServerProfile> findCurrentServerProfile(Server server) {
		String serverName = server.getProject() + "-" + server.getServerType();
		List<ServerProfile> serverProfilesList = serverProfiles.getServerProfiles();
		Optional<ServerProfile> serverProductOptional = serverProfilesList.stream().filter(matchServer(serverName)).findFirst();
		return serverProductOptional;
	}

	private static Predicate<? super ServerProfile> matchServer(String serverName) {
		return srv -> srv.getName().equals(serverName);
	}
}
