/*
   Copyright 2012 David "Destil" Vavra

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.destil.gpsaveraging;

import com.bugsense.trace.BugSenseHandler;

import net.robotmedia.billing.BillingController;
import android.app.Application;

public class GpsAveraging extends Application {
@Override
public void onCreate() {
	super.onCreate();
	// in-app billing init
	BillingController.setConfiguration(new BillingController.IConfiguration() {

		@Override
		public byte[] getObfuscationSalt() {
			return new byte[] { 41, -91, -116, -41, 66, -53, 123, -110, -127, -96, -88, 77, 127, 115, 1, 73, 57, 1, 48,
					-116 };
		}

		@Override
		public String getPublicKey() {
			return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApAI7VpOWr5kaWDxvyi3Vb7R9NjfCfsE76GdWt8z/pq0yuGJjT/DMpWLU8xwy4CYhEkCEpzJsEtpUW5dFgyCzCjSl8uucgOQ75JnhUEB7bQYbmUNLAQDcC6D7IERS29pdHzJksirK/psY9bFSJc6wy1thFAMi3m3HamhtWN7vH+WVvTCb6RL2QLLcu5VMs1rnHQ3/CidLaU4saCMI6AAivfEZqJJa1BUm+sI/w2BeWIC3c430b2nBnpEFePCd7KDXXkVpvdfUhNaA9S2OmJVf60sBsM2++/s+W8YB+PmTP1sgzUsqg4WXH8mD+yz1Gqh7MLqj8t7cx+Ra2Y/6NrqIkwIDAQAB";
		}
		
	});
	// bug sense
	BugSenseHandler.setup(this, "5c1693a4");
}
}
