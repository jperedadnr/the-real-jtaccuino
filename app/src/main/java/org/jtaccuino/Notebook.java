/*
 * Copyright 2024 JTaccuino Contributors
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
package org.jtaccuino;

import java.util.List;
import java.util.Map;

public record Notebook(Map<String, Object> metadata, int nbformat, int nbformat_minor, List<Cell> cells) {

    public static record Cell(String cell_type, Map<String, Object> metadata, String source, List<Output> outputs) {

    }

    public static record Output(String output_type, Map<String, Object> data, Map<String, Object> metadata) {

    }
}
