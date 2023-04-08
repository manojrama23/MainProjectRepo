import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'objectKeys'
})
export class ObjectKeysPipe implements PipeTransform {

    transform(value: any, args?: any): any {
        return Object.keys(value);
    }

}


@Pipe({
    name: 'objectValues'
})
export class ObjectValuesPipe implements PipeTransform {

    transform(value: any, args?: any): any {
        return Object.values(value);
    }

}


@Pipe({
    name: 'objectKeyValues'
})
export class ObjectKeyValuesPipe implements PipeTransform {

    transform(value: any, args?: any): any {
        let keys = Object.keys(value);
        let returnArr = [];
        for(let i = 0; i < keys.length; i++) {
            let tempObj = {
                "key": keys[i],
                "value": value[keys[i]]
            }
            returnArr.push(tempObj);
        }
        return returnArr;
    }

}

