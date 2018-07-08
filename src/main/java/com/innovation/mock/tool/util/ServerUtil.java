package com.innovation.mock.tool.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.innovation.mock.tool.entity.Server;
import com.innovation.mock.tool.entity.ServerProfile;
import com.innovation.mock.tool.entity.ServerProfileCollection;

@Service
public class ServerUtil {

	public static Server updateServerInfo(Server server, ServerProfileCollection serverProfiles) throws Exception {
		ServerProfile serverProfile = findCurrentServerProfile(server, serverProfiles);
		server.setHost(serverProfile.getHost());
		server.setPort(serverProfile.getPort());
		server.setApplication(serverProfile.getEnvironmentApplication());
		server.setSshUsername(serverProfile.getSshUsername());
		server.setSshPassword(serverProfile.getSshPassword());
		server.setProject(StringUtils.split(serverProfile.getName(), "-")[0]);
		server.setContext(serverProfile.getProfileContext());
		server.setUsername(serverProfile.getAuthenUsername());
		server.setPassword(serverProfile.getAuthenPassword());
		return server;
	}

	public static ServerProfile findCurrentServerProfile(Server server, ServerProfileCollection serverProfiles) throws Exception {
		String serverName = server.getProject() + "-" + server.getServerType();
		List<ServerProfile> serverProfilesList = serverProfiles.getServerProfiles();
		Optional<ServerProfile> serverProductOptional = serverProfilesList.stream().filter(matchServer(serverName)).findFirst();
		
		if(serverProductOptional.isPresent()) {
			return serverProductOptional.get();
		}
		throw new Exception();
	}

	private static Predicate<? super ServerProfile> matchServer(String serverName) {
		return srv -> srv.getName().equals(serverName);
	}
}
