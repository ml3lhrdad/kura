/*******************************************************************************
 * Copyright (c) 2011, 2018 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kura.net.admin.modem.telit.de910;

import java.util.Hashtable;

import org.eclipse.kura.net.admin.NetworkConfigurationService;
import org.eclipse.kura.net.admin.modem.CellularModemFactory;
import org.eclipse.kura.net.modem.CellularModem;
import org.eclipse.kura.net.modem.ModemDevice;
import org.eclipse.kura.net.modem.ModemTechnologyType;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.io.ConnectionFactory;
import org.osgi.util.tracker.ServiceTracker;

public class TelitDe910ModemFactory implements CellularModemFactory {

    private static TelitDe910ModemFactory factoryInstance = null;
    private static ModemTechnologyType type = ModemTechnologyType.EVDO;
    private BundleContext bundleContext = null;
    private Hashtable<String, TelitDe910> modemServices = null;
    private ConnectionFactory connectionFactory = null;

    private TelitDe910ModemFactory() {
        this.bundleContext = FrameworkUtil.getBundle(NetworkConfigurationService.class).getBundleContext();

        ServiceTracker<ConnectionFactory, ConnectionFactory> serviceTracker = new ServiceTracker<>(this.bundleContext,
                ConnectionFactory.class, null);
        serviceTracker.open(true);
        this.connectionFactory = serviceTracker.getService();

        this.modemServices = new Hashtable<>();
    }

    public static TelitDe910ModemFactory getInstance() {
        if (factoryInstance == null) {
            factoryInstance = new TelitDe910ModemFactory();
        }
        return factoryInstance;
    }

    @Override
    public CellularModem obtainCellularModemService(ModemDevice modemDevice, String platform) throws Exception {
        String key = modemDevice.getProductName();
        TelitDe910 telitDe910 = this.modemServices.get(key);

        if (telitDe910 == null) {
            telitDe910 = new TelitDe910(modemDevice, platform, this.connectionFactory);
            this.modemServices.put(key, telitDe910);
        } else {
            telitDe910.setModemDevice(modemDevice);
        }

        return telitDe910;
    }

    @Override
    public Hashtable<String, ? extends CellularModem> getModemServices() {
        return this.modemServices;
    }

    @Override
    public void releaseModemService(String usbPortAddress) {
        this.modemServices.remove(usbPortAddress);
    }

    @Override
    @Deprecated
    public ModemTechnologyType getType() {
        return type;
    }
}
