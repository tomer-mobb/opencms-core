/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH & Co. KG (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.jsp.util;

import org.opencms.ade.configuration.CmsADEConfigData;
import org.opencms.file.CmsObject;
import org.opencms.file.types.I_CmsResourceType;
import org.opencms.xml.containerpage.I_CmsFormatterBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Wrapper for resource type information for use in JSPs.
 */
public class CmsResourceTypeInfoWrapper {

    /** The current CMS context. */
    private CmsObject m_cms;

    /** The current sitemap configuration. */
    private CmsADEConfigData m_config;

    /** The wrapped resource type. */
    private I_CmsResourceType m_type;

    /** The active formatters. */
    private List<I_CmsFormatterBean> m_activeFormatters = new ArrayList<>();

    /** The active formatters grouped by container type. */
    private Multimap<String, I_CmsFormatterBean> m_activeFormattersByContainerType = ArrayListMultimap.create();

    /**
     * Creates a new instance.
     *
     * @param cms the current CMS context
     * @param config the current sitemap configuration
     * @param type the type to wrap
     */
    public CmsResourceTypeInfoWrapper(CmsObject cms, CmsADEConfigData config, I_CmsResourceType type) {

        m_cms = cms;
        m_config = config;
        m_type = type;
        for (I_CmsFormatterBean formatter : config.getActiveFormatters().values()) {
            if (!formatter.getResourceTypeNames().contains(type.getTypeName())) {
                continue;
            }
            m_activeFormatters.add(formatter);
            for (String containerType : formatter.getContainerTypes()) {
                m_activeFormattersByContainerType.put(containerType, formatter);
            }

        }
    }

    /**
     * Gets the formatter information beans for a specific container type.
     *
     * @param containerType the container type
     * @return the formatter information
     */
    public List<CmsFormatterInfoWrapper> formatterInfoForContainer(String containerType) {

        return wrapFormatters(m_activeFormattersByContainerType.get(containerType));

    }

    /**
     * Gets the set of container types configured for any active formatters for this resource type.
     *
     * @return the set of container types for formatters
     */
    public Set<String> getFormatterContainerTypes() {

        return Collections.unmodifiableSet(m_activeFormattersByContainerType.keySet());
    }

    /**
     * Gets the formatter information beans for all active formatters for this type.
     *
     * @return the formatter information beans
     */
    public List<CmsFormatterInfoWrapper> getFormatterInfo() {

        return wrapFormatters(m_activeFormatters);

    }

    /**
     * Gets the type name.
     *
     * @return the type name
     */
    public String getName() {

        return m_type.getTypeName();
    }

    /**
     * Wraps a list of formatter beans for use in JSPs.
     *
     * @param formatters the formatters to wrap
     * @return the wrapped formatters
     */
    private List<CmsFormatterInfoWrapper> wrapFormatters(Collection<I_CmsFormatterBean> formatters) {

        return formatters.stream().map(formatter -> new CmsFormatterInfoWrapper(m_cms, m_config, formatter)).collect(
            Collectors.toList());
    }

}
