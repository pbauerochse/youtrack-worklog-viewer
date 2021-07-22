package de.pbauerochse.worklogviewer.datasource.api.domain

import com.fasterxml.jackson.annotation.*

/**
 * https://www.jetbrains.com/help/youtrack/devportal/api-entity-CustomField.html
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "\$type", defaultImpl = SimpleIssueCustomField::class)
@JsonSubTypes(
    value = [
        JsonSubTypes.Type(MultiBuildIssueCustomField::class),
        JsonSubTypes.Type(MultiEnumIssueCustomField::class),
        JsonSubTypes.Type(MultiGroupIssueCustomField::class),
        JsonSubTypes.Type(MultiOwnedIssueCustomField::class),
        JsonSubTypes.Type(MultiUserIssueCustomField::class),
        JsonSubTypes.Type(MultiVersionIssueCustomField::class),

        JsonSubTypes.Type(PeriodIssueCustomField::class),
        JsonSubTypes.Type(SimpleIssueCustomField::class),
        JsonSubTypes.Type(DateIssueCustomField::class),
        JsonSubTypes.Type(SingleBuildIssueCustomField::class),
        JsonSubTypes.Type(SingleEnumIssueCustomField::class),
        JsonSubTypes.Type(SingleGroupIssueCustomField::class),
        JsonSubTypes.Type(SingleOwnedIssueCustomField::class),
        JsonSubTypes.Type(SingleUserIssueCustomField::class),
        JsonSubTypes.Type(SingleVersionIssueCustomField::class),
        JsonSubTypes.Type(StateIssueCustomField::class),
        JsonSubTypes.Type(TextIssueCustomField::class),
        JsonSubTypes.Type(CustomField::class)
    ]
)
abstract class IssueCustomField<T> @JsonCreator constructor(
    @JsonProperty("name") val name: String?,
    @JsonProperty("localizedName") val localizedName: String?,
    val value: T?
) {
    abstract val isMultiValued: Boolean
    abstract val valuesAsString: List<String>
}

abstract class MultiValueIssueCustomField<T>(name: String, localizedName: String?, value: List<T>) : IssueCustomField<List<T>>(name, localizedName, value) {
    override val isMultiValued: Boolean = true
}

@JsonIgnoreProperties(ignoreUnknown = true)
class CustomField @JsonCreator constructor(
    @JsonProperty("name") val name: String,
    @JsonProperty("localizedName") val localizedName: String?
)

class MultiBuildIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: List<BuildBundleElement>
) : MultiValueIssueCustomField<BuildBundleElement>(name, localizedName, value) {
    override val valuesAsString: List<String>
        get() = value?.map { it.name } ?: emptyList()
}

class MultiEnumIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: List<EnumBundleElement>
) : MultiValueIssueCustomField<EnumBundleElement>(name, localizedName, value) {
    override val valuesAsString: List<String>
        get() = value?.map { it.localizedName ?: it.name } ?: emptyList()
}

class MultiGroupIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: List<UserGroup>
) : MultiValueIssueCustomField<UserGroup>(name, localizedName, value) {
    override val valuesAsString: List<String>
        get() = value?.map { it.name } ?: emptyList()
}

class MultiOwnedIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: List<OwnedBundleElement>
) : MultiValueIssueCustomField<OwnedBundleElement>(name, localizedName, value) {
    override val valuesAsString: List<String>
        get() = value?.map { it.name } ?: emptyList()
}

class MultiUserIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: List<User>
) : MultiValueIssueCustomField<User>(name, localizedName, value) {
    override val valuesAsString: List<String>
        get() = value?.map { it.fullName } ?: emptyList()
}

class MultiVersionIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: List<VersionBundleElement>
) : MultiValueIssueCustomField<VersionBundleElement>(name, localizedName, value) {
    override val valuesAsString: List<String>
        get() = value?.map { it.name } ?: emptyList()
}

class PeriodIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: PeriodValue?
) : IssueCustomField<PeriodValue>(name, localizedName, value) {
    override val isMultiValued: Boolean = false
    override val valuesAsString: List<String>
        get() = listOfNotNull(value?.minutes?.toString())
}

open class SimpleIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: Any?
) : IssueCustomField<Any?>(name, localizedName, value) {
    override val isMultiValued: Boolean = false
    override val valuesAsString: List<String>
        get() = listOfNotNull(value?.toString())
}

class DateIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: Long?
) : SimpleIssueCustomField(name, localizedName, value)

abstract class SingleValueIssueCustomField<T>(name: String, localizedName: String?, value: T?) : IssueCustomField<T>(name, localizedName, value) {
    override val isMultiValued: Boolean = false
}

class SingleBuildIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: BuildBundleElement?
) : SingleValueIssueCustomField<BuildBundleElement>(name, localizedName, value) {
    override val valuesAsString: List<String>
        get() = listOfNotNull(value?.name)
}

class SingleEnumIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: EnumBundleElement?
) : SingleValueIssueCustomField<EnumBundleElement>(name, localizedName, value) {
    override val valuesAsString: List<String>
        get() = listOfNotNull(value?.name)
}

class SingleGroupIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: UserGroup?
) : SingleValueIssueCustomField<UserGroup>(name, localizedName, value) {
    override val valuesAsString: List<String>
        get() = listOfNotNull(value?.name)
}

class SingleOwnedIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: OwnedBundleElement?
) : SingleValueIssueCustomField<OwnedBundleElement>(name, localizedName, value) {
    override val valuesAsString: List<String>
        get() = listOfNotNull(value?.name)
}

class SingleUserIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: User?
) : SingleValueIssueCustomField<User>(name, localizedName, value) {
    override val valuesAsString: List<String>
        get() = listOfNotNull(value?.fullName)
}

class SingleVersionIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: VersionBundleElement?
) : SingleValueIssueCustomField<VersionBundleElement>(name, localizedName, value) {
    override val valuesAsString: List<String>
        get() = listOfNotNull(value?.name)
}

class StateIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: StateBundleElement?
) : SingleValueIssueCustomField<StateBundleElement>(name, localizedName, value) {
    override val valuesAsString: List<String>
        get() = listOfNotNull(value?.localizedName ?: value?.name)
}

class TextIssueCustomField @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("value") value: TextFieldValue?
) : IssueCustomField<TextFieldValue>(name, localizedName, value) {
    override val isMultiValued: Boolean = false
    override val valuesAsString: List<String>
        get() = listOfNotNull(value?.text ?: value?.markdownText)
}