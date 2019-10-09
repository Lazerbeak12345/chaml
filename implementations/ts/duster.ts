/**
 * TODO:
 * - [ ] Remove shabang
 * - [x] Remove `//` comments
 * - [ ] Remove `/*` comments
 *   - [ ] (unless converting to documentation?)
 * - [ ] Joining lines ending with `\`
 * - [ ] Remove whitespace line
 */

import * as fs from "fs";
import * as pify from "pify";
let fileLocation=process.argv[2];
/**
 * A promise to get a file.
 * 
 * @param url The url of the file
 */
let getAFile:(url:string)=> Promise<string>
	=url=> pify(fs.readFileSync)(url,'utf8');
getAFile(fileLocation).then((data:string) => {
	let thingsToRemove=[
		/#!\n/,
		/\/\/.*\n/g,
		/\/\*[^[=*/=]]*\*\//g, //needs serius work to make /* * /  */ work
	];
	for (let i=0;i<thingsToRemove.length;++i)
		data=data.replace(thingsToRemove[i],"");
	return data;
}).then(console.log);
