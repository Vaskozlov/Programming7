package collection

import lib.ExecutionStatus
import org.example.database.auth.AuthorizationInfo

interface CollectionInterface {
    fun login(authorizationInfo: AuthorizationInfo)

    fun getInfo(): String

    fun getHistory(): String

    fun getSumOfAnnualTurnover(): Double

    fun maxByFullName(): Organization?

    fun add(organization: Organization)

    fun addIfMax(newOrganization: Organization): ExecutionStatus

    fun modifyOrganization(updatedOrganization: Organization)

    fun removeById(id: Int, creatorId: Int? = null): ExecutionStatus

    fun removeAllByPostalAddress(address: Address, creatorId: Int? = null)

    fun removeHead(creatorId: Int? = null): Organization?

    fun clear(creatorId: Int? = null) : Result<Unit>

    fun toJson(): String
}
