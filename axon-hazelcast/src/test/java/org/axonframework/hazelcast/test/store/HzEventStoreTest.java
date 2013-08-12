/*
 * Copyright (c) 2010-2013. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.hazelcast.test.store;

import com.google.common.collect.Lists;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.GenericDomainEventMessage;
import org.axonframework.domain.SimpleDomainEventStream;
import org.axonframework.eventstore.EventStore;
import org.axonframework.hazelcast.HzProxy;
import org.axonframework.hazelcast.store.HzEventStore;
import org.axonframework.hazelcast.test.HzTestBase;
import org.axonframework.hazelcast.test.domain.HzDomainEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class HzEventStoreTest extends HzTestBase {
    private HzProxy m_proxy = null;

    // *************************************************************************
    //
    // *************************************************************************

    @Before
    public void setUp() throws Exception {
        m_proxy = createHzProxy();
        m_proxy.init();
    }

    @After
    public void tearDown() throws Exception {
        m_proxy.destroy();
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Test
    public void testSaveStreamAndReadBackIn() {
        String     type  = "test";
        String     aid   = UUID.randomUUID().toString();
        EventStore store = new HzEventStore(m_proxy);
        int        evts  = 2;

        List<DomainEventMessage<?>> demWrite = Lists.newArrayListWithCapacity(2);
        for(int i=0;i<evts;i++) {
            demWrite.add(
                new GenericDomainEventMessage<HzDomainEvent>(
                    aid,
                    i,
                    new HzDomainEvent("evt-" + i)));
        }

        store.appendEvents(type,new SimpleDomainEventStream(demWrite));

        List<DomainEventMessage<?>> demRead = Lists.newArrayListWithCapacity(2);
        DomainEventStream des = store.readEvents(type,aid);
        while (des.hasNext()) {
            demRead.add(des.next());
        }

        assertEquals(demWrite.size(),demRead.size());

        for(int i=0;i<evts;i++) {
            assertEquals(
                demWrite.get(i).getIdentifier(),
                demRead.get(i).getIdentifier());
        }
    }
}
