package com.template

import com.template.contract.KYCContract
import com.template.flow.KYCRegisterFlow
import com.template.flow.KYCUpdateFlow
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.StartedMockNode
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class UserUpdateFlowTest {

    private lateinit var network: MockNetwork
    private lateinit var NodeA: StartedMockNode
    private lateinit var NodeB: StartedMockNode


    @Before
    fun setup() {
        network = MockNetwork(listOf("com.template"))
        NodeA = network.createPartyNode(null)
        NodeB = network.createPartyNode(null)
    }

    @After
    fun tearDown() {
        network.stopNodes()
    }


    @Test
    @Throws(Exception::class)
    fun `OneInputShouldBeConsumed`(){
        val flow = KYCRegisterFlow.Initiator("A",5,"ABC","A2","S","C")
        NodeA.startFlow(flow)
        val flow2 = KYCUpdateFlow.Initiator("A","B",5,"ABC","A2","S","C")
        val future = NodeA.startFlow(flow2)
        network.runNetwork()
        val signedTransaction = future.get()
        assertEquals(1,signedTransaction.tx.inputs.size)
    }

    @Test
    @Throws(Exception::class)
    fun `OneOutputShouldBeCreated`(){
        val flow = KYCRegisterFlow.Initiator("A",5,"ABC","A2","S","C")
        NodeA.startFlow(flow)
        val flow2 = KYCUpdateFlow.Initiator("A","B",5,"ABC","A2","S","C")
        val future = NodeA.startFlow(flow2)
        network.runNetwork()
        val signedTransaction = future.get()
        assertEquals(1,signedTransaction.tx.outputs.size)
    }

    @Test
    @Throws(Exception::class)
    fun `transactionConstructedByFlowHasOneUpdateCommand`() {
        val flow = KYCRegisterFlow.Initiator("A",5,"ABC","A2","S","C")
        NodeA.startFlow(flow)
        val flow2 = KYCUpdateFlow.Initiator("A","B",5,"ABC","A2","S","C")
        val future = NodeA.startFlow(flow2)
        network.runNetwork()
        val signedTransaction = future.get()
        Assert.assertEquals(1, signedTransaction.tx.commands.size)
        val (value) = signedTransaction.tx.commands[0]
        assert(value is KYCContract.Commands.Update)
    }
}