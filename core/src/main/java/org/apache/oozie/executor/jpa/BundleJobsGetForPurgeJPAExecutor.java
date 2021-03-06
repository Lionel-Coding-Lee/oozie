/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.oozie.executor.jpa;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.oozie.ErrorCode;

/**
 * Load the list of completed BundleJob for purge ready.
 */
public class BundleJobsGetForPurgeJPAExecutor implements JPAExecutor<List<String>> {

    private static final long DAY_IN_MS = 24 * 60 * 60 * 1000;
    private long olderThanDays;
    private int limit;
    private int offset;

    public BundleJobsGetForPurgeJPAExecutor(long olderThanDays, int limit) {
        this(olderThanDays, 0, limit);
    }

    public BundleJobsGetForPurgeJPAExecutor(long olderThanDays, int offset, int limit) {
        this.olderThanDays = olderThanDays;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public String getName() {
        return "BundleJobsGetForPurgeJPAExecutor";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> execute(EntityManager em) throws JPAExecutorException {
        List<String> bundleJobs = null;
        try {
            Timestamp lastModTm = new Timestamp(System.currentTimeMillis() - (olderThanDays * DAY_IN_MS));
            Query jobQ = em.createNamedQuery("GET_COMPLETED_BUNDLE_JOBS_OLDER_THAN");
            jobQ.setParameter("lastModTime", lastModTm);
            jobQ.setMaxResults(limit);
            jobQ.setFirstResult(offset);
            bundleJobs = jobQ.getResultList();
        }
        catch (Exception e) {
            throw new JPAExecutorException(ErrorCode.E0603, e.getMessage(), e);
        }
        return bundleJobs;
    }

}
