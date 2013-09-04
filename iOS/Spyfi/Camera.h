//
//  Camera.h
//  Spyfi
//
//  Created by Nic Raboy on 6/21/13.
//  Copyright (c) 2013 Nic Raboy. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface Camera : NSManagedObject

@property (nonatomic, retain) NSString * title;
@property (nonatomic, retain) NSString * host;
@property (nonatomic, retain) NSNumber * port;
@property (nonatomic, retain) NSString * username;
@property (nonatomic, retain) NSString * password;

@end
