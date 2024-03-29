/*
 * Copyright (C) openrsc 2009-13 All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by openrsc Team <dev@openrsc.com>, January, 2013
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package org.openrsc.server.packethandler;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.LoginResponse;
import org.openrsc.server.ServerBootstrap;
import org.openrsc.server.database.game.Login;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.packetbuilder.RSCPacketBuilder;
import org.openrsc.server.util.DataConversions;

/**
 * A message handler for logging a player in.  Before a player can log in, 
 * they have to be validated.  Validation has several phases including:
 * <ul>
 * 	<li>Verifying correct client version</li>
 * 	<li>Verifying correct session keys</li>
 * 	<li>Additional validation performed in a DatabaseService</li>
 * </ul>
 * 
 * @author Zilent
 * 
 * @version 1.1, 2/1/2013
 * 
 * @since 3.0
 * 
 */
public class PlayerLogin
	implements
		PacketHandler
{
	
	public void handlePacket(Packet p, IoSession session)
		throws
			Exception
	{
		p.skip(1);
		int clientVersion = p.readShort();
		if(clientVersion != Config.getServerVersion())
		{
			session.write(new RSCPacketBuilder().setBare(true).addByte((byte)LoginResponse.CLIENT_NOT_UP_TO_DATE.ordinal()).toPacket());
			session.close();
			return;
		}
		long[] sessionKeys = new long[2];
		for (int key = 0; key < sessionKeys.length; key++)
        {
            long sessionKey = p.readLong();
            //System.out.println("sessionKey[" + key + "]: " + sessionKey);
			sessionKeys[key] = sessionKey;
        }
        
		long serverKey = (Long)session.getAttachment();
        long compareKey = sessionKeys[0];
        
        //System.out.println("sesssion.getAttachment() serverKey: " + serverKey);
        //System.out.println("sesssion.getAttachment() compareKey: " + compareKey);
        
		if(serverKey != compareKey)
		{
			session.write(new RSCPacketBuilder().setBare(true).addByte((byte)LoginResponse.SESSION_REJECTED.ordinal()).toPacket());
			session.close();
			return;
		}
		int clientWidth = p.readInt();
        
		RSCPacket loginPacket = DataConversions.decryptRSA(p.readBytes(p.readByte()));
        
		String username = loginPacket.readString(20).trim();
		loginPacket.skip(1);
		String password = loginPacket.readString(20).trim();
		loginPacket.skip(1);
        
		ServerBootstrap.getDatabaseService().submit(new Login(username, password, session, clientWidth));
	}
}