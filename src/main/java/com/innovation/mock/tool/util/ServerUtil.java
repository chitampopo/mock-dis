package com.innovation.mock.tool.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import com.innovation.mock.tool.entity.Server;
import com.innovation.mock.tool.entity.ServerProfile;
import com.innovation.mock.tool.entity.ServerProfileCollection;

@Service
public class ServerUtil {

	public static Server updateServerInfo(Server server, ServerProfileCollection serverProfiles) {
		Optional<ServerProfile> serverProfile = findCurrentServerProfile(server, serverProfiles);
		
		if(serverProfile.isPresent()) {
			server.setHost(serverProfile.get().getHost());
			server.setPort(serverProfile.get().getPort());
			server.setApplication(serverProfile.get().getEnvironmentApplication());
			server.setSshUsername(serverProfile.get().getSshUsername());
			server.setSshPassword(serverProfile.get().getSshPassword());
			server.setProject(serverProfile.get().getEnvironmentApplication());
			server.setContext(serverProfile.get().getProfileContext());
			server.setUsername(serverProfile.get().getAuthenUsername());
			server.setPassword(serverProfile.get().getAuthenPassword());
		}
		return server;
	}

	public static Optional<ServerProfile> findCurrentServerProfile(Server server, ServerProfileCollection serverProfiles) {
		String serverName = server.getProject() + "-" + server.getServerType();
		List<ServerProfile> serverProfilesList = serverProfiles.getServerProfiles();
		Optional<ServerProfile> serverProductOptional = serverProfilesList.stream().filter(matchServer(serverName)).findFirst();
		return serverProductOptional;
	}

	private static Predicate<? super ServerProfile> matchServer(String serverName) {
		return srv -> srv.getName().equals(serverName);
	}
}
