/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class BootstrapUiControlAdapter
{

    private target: any;

    public constructor( obj: any )
    {
        this.target = obj;
    }

    public getValue(): any
    {
        return this.target;
    }

    public selectValue(): any
    {
        return this.target;
    }

    public onValueChange(): any
    {
        return this.target;
    }

    public reset()
    {

    }

    public getTarget(): any
    {
        return this.target;
    }

}