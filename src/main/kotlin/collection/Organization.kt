package collection

import exceptions.IllegalArgumentsForOrganizationException
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Organization(
    var id: Int?,
    var name: String?,
    var coordinates: Coordinates?,
    var creationDate: LocalDate?,
    var annualTurnover: Double?,
    var fullName: String?,
    var employeesCount: Int?,
    var type: OrganizationType?,
    var postalAddress: Address?,
    var creatorId: Int? = null,
) : Comparable<Organization> {
    fun validate() {
        val validationResult = checkCorrectness()

        if (!validationResult.isValid) {
            throw IllegalArgumentsForOrganizationException(validationResult.reason)
        }

        postalAddress?.validate()
    }

    fun optimize() {
        postalAddress = postalAddress?.simplify()
    }

    fun fillNullFromAnotherOrganization(organization: Organization) {
        id = id ?: organization.id
        name = name ?: organization.name
        coordinates = fillCoordinatesWithMissedInformation(coordinates, organization.coordinates)
        creationDate = creationDate ?: organization.creationDate
        annualTurnover = annualTurnover ?: organization.annualTurnover
        fullName = fullName ?: organization.fullName
        employeesCount = employeesCount ?: organization.employeesCount
        type = type ?: organization.type
        postalAddress = fillAddressWithMissedInformation(postalAddress, organization.postalAddress)
    }

    fun toPairOfFullNameAndType(): Pair<String?, OrganizationType?> {
        return fullName to type
    }

    class ValidationResult {
        private var errorReason: String? = null

        constructor()

        constructor(errorReason: String?) {
            this.errorReason = errorReason
        }

        val isValid: Boolean
            get() = errorReason == null || errorReason!!.isEmpty()

        val reason: String
            get() {
                if (errorReason == null) {
                    throw RuntimeException()
                }

                return errorReason as String
            }
    }

    override fun compareTo(other: Organization): Int {
        val result = fullName!!.compareTo(other.fullName!!)
        return if (result == 0 && type != null) type!!.compareTo(other.type!!) else result
    }

    private fun checkCorrectness(): ValidationResult {
        if (name.isNullOrEmpty()) {
            return ValidationResult("Name must not be empty")
        }

        if (annualTurnover!! <= 0.0) {
            return ValidationResult("Annual turnover must be above zero")
        }

        if (fullName!!.length > 573) {
            return ValidationResult("Full name too long, it's length must be within 573 symbols")
        }

        if (employeesCount != null && employeesCount!! < 0) {
            return ValidationResult("Employees count must not be negative")
        }

        return ValidationResult()
    }
}