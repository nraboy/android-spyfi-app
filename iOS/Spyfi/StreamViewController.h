// Spyfi
// Created by Nic Raboy

#import <UIKit/UIKit.h>

@interface StreamViewController : UIViewController
- (IBAction)navigationBack:(id)sender;

@property (strong, nonatomic) id cameraItem;
@property (weak, nonatomic) IBOutlet UIImageView *cameraImage;

@property (weak, nonatomic) NSString *streamUrl;

@end
