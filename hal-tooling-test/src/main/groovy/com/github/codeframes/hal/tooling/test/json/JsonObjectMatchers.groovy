/*
 * Copyright © 2016 Richard Burrow (https://github.com/codeframes)
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
package com.github.codeframes.hal.tooling.test.json

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.StringDescription

/**
 * A collection of Hamcrest Matcher's for JSON assertions.
 */
class JsonObjectMatchers {

    /**
     * Returns a Matcher for asserting if a provided JSON Array matches that of jsonArray, ignoring the ordering
     * elements.
     *
     * @param jsonArray the expected JSON Array to match against ignoring the ordering of the elements
     * @return a Matcher to assert if a provided JSON Array matches that of jsonArray, ignoring the ordering elements
     */
    static Matcher inAnyOrder(String jsonArray) {
        return new UnorderedListMatcher(JsonUtils.toJsonObj(jsonArray) as List)
    }

    /**
     * ul (Unordered List), an alias of {@link #inAnyOrder(java.lang.String)} with a minimalist name for convenient
     * embedding in {@link JSON#JSON(GString)}.
     */
    static Matcher ul(String jsonArray) {
        return inAnyOrder(jsonArray)
    }

    /**
     * Returns a Matcher for asserting if a provided JSON Array matches that of jsonArray, ignoring the ordering elements.
     * In addition supports templated values as described by {@link JsonUtils#toJsonObj(GString json)}.
     *
     * @param jsonArray the expected JSON Array to match against ignoring the ordering of the elements
     * @return a Matcher to assert if a provided JSON Array matches that of jsonArray, ignoring the ordering elements
     */
    static Matcher inAnyOrder(GString jsonArray) {
        return new UnorderedListMatcher(JsonUtils.toJsonObj(jsonArray) as List)
    }

    /**
     * ul (Unordered List), an alias of {@link #inAnyOrder(groovy.lang.GString)} with a minimalist name for convenient
     * embedding in {@link JSON#JSON(GString)}.
     */
    static Matcher ul(GString jsonArray) {
        return inAnyOrder(jsonArray)
    }

    static class UnorderedListMatcher extends BaseMatcher {

        private final List expected

        UnorderedListMatcher(List expected) {
            this.expected = expected
        }

        @Override
        boolean matches(Object actual) {
            List actualList = (List) actual
            for (Object expectedItem : expected) {
                boolean found = false
                for (Object actualItem : actualList) {
                    if (actualItem == expectedItem) {
                        found = true
                        break
                    } else if (expectedItem instanceof Map) {
                        if (equalTo(expectedItem).matches(actualItem)) {
                            found = true
                            break
                        }
                    } else if (expectedItem instanceof List) {
                        if (equalTo(expectedItem).matches(actualItem)) {
                            found = true
                            break
                        }
                    } else {
                        return false
                    }
                }
                if (!found) {
                    return false
                }
            }
            return true
        }

        @Override
        void describeTo(Description description) {
            description.appendText("to contain in no particular order ").appendValue(expected)
        }

        @Override
        void describeMismatch(Object item, Description description) {
            description.appendText("was ").appendValue(item)
        }

        @Override
        public String toString() {
            return expected.toString()
        }
    }

    /**
     * Returns a Matcher for asserting if a provided JSON Object matches that of jsonObj.
     *
     * @param jsonObj the expected JSON Object to match against
     * @return a Matcher to assert if a provided JSON Object matches that of jsonObj
     */
    static Matcher<Map> equalTo(Map jsonObj) {
        return new JsonObjectEqualTo(jsonObj, true)
    }

    /**
     * Returns a Matcher for asserting if a provided JSON Array matches that of jsonArray.
     *
     * @param jsonArray the expected JSON Array to match against
     * @return a Matcher to assert if a provided JSON Array matches that of jsonArray
     */
    static Matcher<List> equalTo(List jsonArray) {
        return new JsonArrayEqualTo(jsonArray, true)
    }

    static class JsonObjectEqualTo extends JsonObjectBaseMatcher {

        private final Map expected

        JsonObjectEqualTo(Map expected) {
            this(expected, false)
        }

        JsonObjectEqualTo(Map expected, boolean root) {
            super(root)
            this.expected = expected
        }

        @Override
        boolean matches(Object actual) {

            if (!isMap(actual)) {
                return false
            }

            Map actualMap = actual as Map
            if (actualMap == expected) {
                return true
            }

            for (expectedEntry in expected) {

                String field = expectedEntry.key
                String fieldLocator = field

                def expectedValue = expectedEntry.value
                if (!hasKey(actualMap, field, expectedValue)) {
                    return false
                }

                def actualValue = actualMap[field]
                if (!elementMatches(fieldLocator, actualValue, expectedValue, true)) {
                    return false
                }
            }
            return true
        }

        boolean isMap(Object actual) {
            boolean isMap = actual instanceof Map
            if (!isMap) {
                this.describeToDescription = newDescription().appendText("' value as a Map")
                this.describeMismatchDescription = new StringDescription().appendText("found ").appendValue(actual)
            }
            return isMap
        }

        boolean hasKey(Map actualMap, String key, Object expectedValue) {
            boolean hasKey = actualMap.containsKey(key)
            if (!hasKey) {
                this.describeToDescription = newDotPrefixedDescription().appendText(key).appendText("' == ").appendValue(expectedValue)
                this.describeMismatchDescription = new StringDescription().appendText("field does not exist")
            }
            return hasKey
        }

        @Override
        public String toString() {
            return expected.toString()
        }
    }

    static class JsonArrayEqualTo extends JsonObjectBaseMatcher {

        private final List expected

        JsonArrayEqualTo(List expected) {
            this(expected, false)
        }

        JsonArrayEqualTo(List expected, boolean root) {
            super(root)
            this.expected = expected
        }

        @Override
        boolean matches(Object actual) {

            if (!isList(actual)) {
                return false
            }

            List actualList = actual as List
            if (actualList == expected) {
                return true
            }

            if (!sameSize(actualList, expected)) {
                return false
            }

            for (int index = 0; index < expected.size(); index++) {
                String fieldLocator = "[$index]"

                def expectedValue = expected.get(index)
                def actualValue = actualList.get(index)
                if (!elementMatches(fieldLocator, actualValue, expectedValue, false)) {
                    return false
                }
            }
            return true
        }

        boolean isList(Object actual) {
            boolean isList = actual instanceof List
            if (!isList) {
                this.describeToDescription = newDescription().appendText("' value as a List")
                this.describeMismatchDescription = new StringDescription().appendText("found ").appendValue(actual)
            }
            return isList
        }

        boolean sameSize(List actualList, List expectedList) {
            boolean sameSize = actualList.size() == expectedList.size()
            if (!sameSize) {
                this.describeToDescription = newDescription().appendText("' as a List of size ").appendValue(expectedList.size())
                this.describeMismatchDescription = new StringDescription().appendText("was ").appendValue(actualList.size())
            }
            return sameSize
        }

        @Override
        public String toString() {
            return expected.toString()
        }
    }

    static abstract class JsonObjectBaseMatcher extends BaseMatcher {

        protected final boolean root
        protected Description describeToDescription
        protected Description describeMismatchDescription

        JsonObjectBaseMatcher(boolean root) {
            this.root = root
        }

        @Override
        void describeTo(Description description) {
            description.appendText(describeToDescription.toString())
        }

        @Override
        void describeMismatch(Object item, Description description) {
            description.appendText(describeMismatchDescription.toString())
        }

        boolean elementMatches(String fieldLocator, Object actualValue, Object expectedValue, boolean addDotPrefix) {

            if (actualValue == expectedValue) {
                return true

            } else if (expectedValue instanceof Matcher) {
                Matcher expectedValueMatcher = expectedValue
                return matches(expectedValueMatcher, fieldLocator, actualValue, addDotPrefix)

            } else if (expectedValue instanceof Map) {
                def jsonObjectEqualTo = new JsonObjectEqualTo(expectedValue)
                return matches(jsonObjectEqualTo, fieldLocator, actualValue, addDotPrefix)

            } else if (expectedValue instanceof List) {
                def jsonArrayEqualTo = new JsonArrayEqualTo(expectedValue)
                return matches(jsonArrayEqualTo, fieldLocator, actualValue, addDotPrefix)

            } else {
                setMismatchItems(fieldLocator, actualValue, expectedValue, addDotPrefix)
                return false
            }
        }

        boolean matches(Matcher expectedValueMatcher, String fieldLocator, Object actualValue, boolean addDotPrefix) {
            boolean result = expectedValueMatcher.matches(actualValue)
            if (!result) {
                setMismatchItems(fieldLocator, actualValue, expectedValueMatcher, addDotPrefix)
            }
            return result
        }

        void setMismatchItems(String field, Object actual, Matcher expectedValueMatcher, boolean addDotPrefix) {
            def describeToDescription = newDescription(addDotPrefix).appendText(field)
            if (!(expectedValueMatcher instanceof JsonObjectBaseMatcher)) {
                describeToDescription.appendText("' ")
            }

            expectedValueMatcher.describeTo(describeToDescription)
            this.describeToDescription = describeToDescription

            def describeMismatchDescription = new StringDescription()
            expectedValueMatcher.describeMismatch(actual, describeMismatchDescription)
            this.describeMismatchDescription = describeMismatchDescription
        }

        void setMismatchItems(String field, Object actual, Object expected, boolean addDotPrefix) {
            this.describeToDescription = newDescription(addDotPrefix).appendText(field).appendText("' == ").appendValue(expected)
            this.describeMismatchDescription = new StringDescription().appendText("was ").appendValue(actual)
        }

        Description newDotPrefixedDescription() {
            return newDescription(true)
        }

        Description newDescription() {
            return newDescription(false)
        }

        Description newDescription(boolean addDotPrefix) {
            def describeToDescription = new StringDescription()
            if (root) {
                describeToDescription.appendText("'")
            } else if (addDotPrefix) {
                describeToDescription.appendText(".")
            }
            return describeToDescription
        }
    }
}
