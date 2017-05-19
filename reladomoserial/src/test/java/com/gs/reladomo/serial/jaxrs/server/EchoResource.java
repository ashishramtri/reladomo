package com.gs.reladomo.serial.jaxrs.server;

import com.gs.fw.common.mithra.MithraManagerProvider;
import com.gs.fw.common.mithra.MithraTransaction;
import com.gs.fw.common.mithra.MithraTransactionalObject;
import com.gs.fw.common.mithra.TransactionalCommand;
import com.gs.fw.common.mithra.test.domain.Account;
import com.gs.fw.common.mithra.test.domain.Order;
import com.gs.fw.common.mithra.test.domain.OrderFinder;
import com.gs.fw.common.mithra.util.serializer.SerializationConfig;
import com.gs.fw.common.mithra.util.serializer.Serialized;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/echo")
public class EchoResource
{
    @Path("/order")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response echoOrder(Serialized<Order> order)
    {
        SerializationConfig config = SerializationConfig.shallowWithDefaultAttributes(OrderFinder.getFinderInstance());
        return Response.ok().entity(new Serialized<Order>(order.getWrapped(), config)).build();
    }

    @Path("/account")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response echoAccount(Serialized<Account> account)
    {
        return Response.ok().entity(account).build();
    }

    @Path("/orderOne")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Serialized<Order> firstOrder()
    {
        SerializationConfig config = SerializationConfig.shallowWithDefaultAttributes(OrderFinder.getFinderInstance());
        return new Serialized((OrderFinder.findOne(OrderFinder.orderId().eq(1))), config);
    }

    @Path("/writeOrder")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void writeOrder(final Serialized<Order> order)
    {
        MithraManagerProvider.getMithraManager().executeTransactionalCommand(new TransactionalCommand<Object>()
        {
            @Override
            public Object executeTransaction(MithraTransaction tx) throws Throwable
            {
                order.getWrapped().copyDetachedValuesToOriginalOrInsertIfNew();
                return null;
            }
        });


    }

    @Path("/writeAnything")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void writeAnything(final Serialized<? extends MithraTransactionalObject> anything)
    {
        MithraManagerProvider.getMithraManager().executeTransactionalCommand(new TransactionalCommand<Object>()
        {
            @Override
            public Object executeTransaction(MithraTransaction tx) throws Throwable
            {
                anything.getWrapped().copyDetachedValuesToOriginalOrInsertIfNew();
                return null;
            }
        });


    }

}